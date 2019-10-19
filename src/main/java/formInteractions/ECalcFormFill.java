package formInteractions;

import dataOutput.CalcOutput;
import dataParsing.DataSetLoader;
import dataParsing.PropellerDataLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A configuration to fill https://www.ecalc.ch/torquecalc.php by
 */
class ECalcFormFill extends PageFormFill {

    private static final double MAX_VOLTAGE = 22.2;

    /**
     * Calls for the input of data into ECalc, then calls for the creation of the output from ECalc
     * @param form The ECalc form to fill
     * @param dataSet The propeller data to fill the form with
     */
    @Override
    void runFormInput(PageForm form, DataSetLoader dataSet) {

        //Loops through every line of every RPM data of every propeller file
        for (int i = 0; i < ((PropellerDataLoader) dataSet).getNumOfDiffProps(); i++) {
            String propName = ((PropellerDataLoader) dataSet).getPropTableName(i);
            System.out.println("Writing matches for prop: " + propName);

            for (int propRPM : ((PropellerDataLoader) dataSet).getPropRPMs(i)) {

                //Loops through, then inputs relevent propeller data from every line into ecalc, then calls for output
                for (int q = 0; q < ((PropellerDataLoader) dataSet).getPropDataNumOfRows(i, propRPM); q++) {
                    double torqueValue = ((PropellerDataLoader) dataSet).getTorqueValue(i, propRPM, q);
                    if (torqueValue > 0.0) {
                        //inputECalcFields(form, torqueValue, propRPM);
                        CalcOutput.writeCalcOutput(propName, propRPM, scrubECalcOutput());
                    }
                }

            }
        }
    }

    /** TODO: Implement
     * Scrubs the motor matches from ECalc and stores it into a 2d array
     * @return The motor matches
     */
    @Nullable
    @Contract(pure = true)
    private String[][] scrubECalcOutput() {
        return null;
    }

    /**
     * Inputs the given data to https://www.ecalc.ch/torquecalc.php in a sequential manner.
     * @param form The form to fill
     * @param torqueValue The torque value of the propeller
     * @param rpmValue The RPM of the propeller for the given torque data-point
     */
    private void inputECalcFields(@NotNull PageForm form, double torqueValue, int rpmValue) {
        form.clickButton("modalConfirmOk");

        form.fillField("inRTorquelbft", torqueValue);
        form.fillField("inRRpm", rpmValue);
        form.fillField("inRUmax", MAX_VOLTAGE);

        form.selectDropdownValue("inMManufacturer", "any");
        form.selectDropdownValue("inMType", "any");
        form.selectDropdownValue("inGMotorCooling", "excellent");
    }
}
