package dataParsing;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import static dataParsing.PropellerDataLoader.MAX_FORWARD_AIRSPEED;
import static dataParsing.PropellerDataLoader.POWER_CONSTANT;

/**
 * Organizes a propeller data file into a LinkedHashMap(Integer (RPM), double[][] (actual data)) structure
 */
public class PropellerDataSet {

    //Interpolator used for any linear interpolations
    private static final LinearInterpolator interpolator = new LinearInterpolator();

    //The name of this propeller
    private final String name;

    //The main data structure that holds all the propeller data for this propeller
    private final LinkedHashMap<Integer, double[][]> mappedData;

    private double staticThrust;

    //Table indexes
    private static final int VELOCITY = 0;
    private static final int ADVANCE_RATIO = 1;
    private static final int EFFICIENCY = 2;
    private static final int THRUST_COEFFICIENT = 3;
    private static final int POWER_COEFFICIENT = 4;
    private static final int POWER = 5;
    private static final int TORQUE = 6;
    private static final int THRUST = 7;

    /**
     * Organizes propeller data upon object creation
     * @param propName The name of the propeller associated with this object
     * @param rawData The parsed (cleaned) unorganized data from the data-set loader
     */
    PropellerDataSet(String propName, ArrayList<String[]> rawData) {
        System.out.print("Created data set for " + propName + ", organizing data ...");
        this.name = propName;
        this.mappedData = new LinkedHashMap<>();
        organizeData(rawData);
        System.out.print("Data organized! \n");
    }

    /**
     * Organizes rawData into the mappedData structure.
     * @param rawData The given rawData
     */
    protected void organizeData(@NotNull ArrayList<String[]> rawData) {
        int tableStartIndex = 1;
        int tableEndIndex = 1;

        for (int i = 0; i < rawData.size(); i++) {
            if (rawData.get(i).length == 1) {
                tableEndIndex = i;
                if (tableEndIndex - tableStartIndex > 0) {
                    mappedData.put(
                        Integer.parseInt(rawData.get(tableStartIndex - 1)[0]),
                        createDataArray(rawData, tableStartIndex, tableEndIndex)
                    );
                }
                tableStartIndex = i + 1;
            }
            else {
                tableEndIndex++;
            }
        }
    }

    /**
     * Creates a 2D array from an entire (specified) RPM table (excluding the RPM value itself)
     * @param rawData All of the rawData
     * @param tableStartIndex (Inclusive) The start of the relevant RPM table data
     * @param tableEndIndex (Inclusive) The end of the relevant RPM table data
     * @return The populated 2D array containing exactly one complete RPM table
     */
    @NotNull
    private double[][] createDataArray(@NotNull ArrayList<String[]> rawData, int tableStartIndex, int tableEndIndex) {
        if (rawData.get(1).length <= 1) {
            tableStartIndex++;
        }

        double[][] dataArray = new double[tableEndIndex - tableStartIndex][rawData.get(tableStartIndex).length];
        for (int i = tableStartIndex; i < tableEndIndex; i++) {
            if (rawData.get(i).length <= 1) {
                System.out.println("Next set ...");
                break;
            }
            else {
                for (int k = 0; k < rawData.get(i).length; k++) {
                    if (!rawData.get(i)[k].equals("-")) {
                        dataArray[i - tableStartIndex][k] = Double.parseDouble(rawData.get(i)[k]);
                    }
                }
            }
        }
        return dataArray;
    }

    /**
     * @return The propeller name associated with this data-set organizer
     */
    public String getName() {
        return name;
    }

    /**
     * @return All RPM values of this propeller data file
     */
    public Set<Integer> getPropRPMs() {
        return mappedData.keySet();
    }

    /**
     * @param propRPM The RPM to match with RPM data table
     * @param propRPMTableRowNum The line number of a given (by index proxy) RPM data table
     * @param index The index of the table line to get
     * @return Whatever value is at the given indexes
     */
    private double getTableValue(int propRPM, int propRPMTableRowNum, int index) {
        return mappedData.get(propRPM)[propRPMTableRowNum][index];
    }

    /**
     * Gets the number of rows in a given RPM table data set
     * @param propRPM The RPM table to look at
     * @return The num of rows in the given RPM table
     */
    private int getRPMTableNumOfRows(int propRPM) {
        return mappedData.get(propRPM).length;
    }

    /**
     * Finds the closest two velocity line numbers in the RPM data table to the given velocity
     * @param velocity The given velocity to compare against
     * @param propRPM The RPM whose table is used
     * @return The two closest velocity value line numbers
     */
    private int[] findClosestVelocities(double velocity, int propRPM) {

        BigDecimal realVelocity = BigDecimal.valueOf(velocity);
        int[] closestVelocities = new int[2];

        for (int i = 0; i < getRPMTableNumOfRows(propRPM); i++) {

            BigDecimal tableVelocity = BigDecimal.valueOf(getTableValue(propRPM, i, VELOCITY));

            if (tableVelocity.max(realVelocity).equals(tableVelocity)) {
                closestVelocities[1] = i;
                closestVelocities[0] = i - 1;
                break;
            }
        }
        return closestVelocities;
    }

    /**
     * Interpolates either power or thrust at the given velocity using the given propRPM's data table
     * @param targetVelocity The velocity to interpolate at
     * @param propRPM The RPM whose table to use
     * @param thrustInterpolate Whether or not to interpolate thrust. Interpolates power if false
     * @return The interpolated either power or thrust number at the given velocity
     */
    private double interpolateAtVelocity(double targetVelocity, int propRPM, boolean thrustInterpolate) {
        int[] closestVelocities = findClosestVelocities(targetVelocity, propRPM);

        double[] x = new double[2];
        double[] y = new double[2];

        if (closestVelocities[0] > -1 && closestVelocities[1] > -1 && (closestVelocities[0] != closestVelocities[1])) {
            x[0] = getTableValue(propRPM, closestVelocities[0], VELOCITY);
            x[1] = getTableValue(propRPM, closestVelocities[1], VELOCITY);

            if (thrustInterpolate) {
                y[0] = getTableValue(propRPM, closestVelocities[0], THRUST);
                y[1] = getTableValue(propRPM, closestVelocities[1], THRUST);
            }
            else {
                y[0] = getTableValue(propRPM, closestVelocities[0], POWER);
                y[1] = getTableValue(propRPM, closestVelocities[1], POWER);
            }

            return interpolator.interpolate(x, y).value(targetVelocity);
        }
        return 0;
    }

    /**
     * Interpolates dynamic thrust at the given velocity using rpm1 and rpm2's respective data tables
     * @param velocity The velocity to get thrust numbers at
     * @param rpm1 The lower RPM to use to interpolate data from
     * @param rpm2 The higher RPM to use to interpolate data from
     * @return The dynamic thrust number at the given velocity
     */
    public double getDynamicThrust(int velocity, int rpm1, int rpm2) {
        double[] x = new double[2];
        double[] y = new double[2];

        x[0] = interpolateAtVelocity(velocity, rpm1, false);
        x[1] = interpolateAtVelocity(velocity, rpm2, false);

        y[0] = interpolateAtVelocity(velocity, rpm1, true);
        y[1] = interpolateAtVelocity(velocity, rpm2, true);

        if (x[1] > POWER_CONSTANT && x[0] < POWER_CONSTANT) {
            return interpolator.interpolate(x, y).value(POWER_CONSTANT);
        }
        else {
            return -1;
        }
    }

    /**
     * Predicts dynamic thrust at the given velocity using constants and the static thrust
     * @param velocity The velocity to predict at
     * @return The dynamic thrust prediction
     */
    public double getDynamicThrustPrediction(int velocity) {
        return staticThrust - ((staticThrust * velocity) / MAX_FORWARD_AIRSPEED);
    }

    /**
     * Interpolates an RPM at the given velocity using interpolated power numbers from the given rpms
     * @param velocity The velocity to interpolate at
     * @param rpm1 The lower bound RPM
     * @param rpm2 The higher bound RPM
     * @return The interpolated RPM number
     */
    public double InterpolateRPM(int velocity, int rpm1, int rpm2) {
        double[] x = new double[2];
        double[] y = new double[2];

        x[0] = interpolateAtVelocity(velocity, rpm1, false);
        x[1] = interpolateAtVelocity(velocity, rpm2, false);

        y[0] = rpm1;
        y[1] = rpm2;

        if (x[1] > POWER_CONSTANT && x[0] < POWER_CONSTANT) {
            return interpolator.interpolate(x, y).value(POWER_CONSTANT);
        }
        else {
            return -1;
        }
    }

    /**
     * Interpolates the static thrust at the RPMs that are closest to the max output of the motor (POWER_CONSTANT)
     * @return The interpolated static thrust
     */
    public double getStaticThrust() {
        ArrayList<Integer> propRPMS = new ArrayList<>(getPropRPMs());

        for (int i = 0; i < propRPMS.size(); i++) {
            double currentPower = getTableValue(propRPMS.get(i), 0, POWER);

            if (currentPower > POWER_CONSTANT) {
                double[] x = new double[2];
                double[] y = new double[2];

                x[0] = getTableValue(propRPMS.get(i - 1), 0, POWER);
                x[1] = currentPower;
                y[0] = getTableValue(propRPMS.get(i - 1), 0, THRUST);
                y[1] = getTableValue(propRPMS.get(i), 0, THRUST);

                PolynomialSplineFunction test = interpolator.interpolate(x, y);
                staticThrust = test.value(POWER_CONSTANT);
                return staticThrust;
            }
        }
        return 0;
    }
}