package org.firstinspires.ftc.teamcode.Encoder_Drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "EncoderAutonSimple", group = "Auto")
public class EncoderAutonTemplate extends LinearOpMode {
    private DcMotorEx leftFront, rightFront, leftBack, rightBack, intakeMotor;

    // Minimal robot constants - set these for your robot
    private static final int CPR = 560;           // encoder counts per motor rev
    private static final double WHEEL_DIAMETER_IN = 4.0; // inches
    private double ticksPerInch;

    @Override
    public void runOpMode() {
        leftFront  = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftBack   = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightBack  = hardwareMap.get(DcMotorEx.class, "rightBack");
        intakeMotor= hardwareMap.get(DcMotorEx.class, "intakeMotor");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        ticksPerInch = (CPR) / (Math.PI * WHEEL_DIAMETER_IN);

        // reset once
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();
        if (!opModeIsActive()) return;

        // Example sequence (replace values as needed)
        driveDistanceInches(24, 0.5, 5.0);
        runIntake(0.8, 2.0);
        turnDegrees(90, 0.4, 4.0);
        strafeDistanceInches(12, 0.5, 4.0);

        stopAllDrive();
    }

    private int inchesToTicks(double inches) {
        return (int)Math.round(inches * ticksPerInch);
    }

    private void setTargets(int lf, int lb, int rf, int rb) {
        leftFront.setTargetPosition(lf);
        leftBack.setTargetPosition(lb);
        rightFront.setTargetPosition(rf);
        rightBack.setTargetPosition(rb);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void waitForBusyOrTimeout(double timeoutSec) {
        ElapsedTime t = new ElapsedTime();
        while (opModeIsActive() && t.seconds() < timeoutSec &&
                (leftFront.isBusy() || leftBack.isBusy() || rightFront.isBusy() || rightBack.isBusy())) {
            telemetry.addData("LF pos", leftFront.getCurrentPosition());
            telemetry.addData("RF pos", rightFront.getCurrentPosition());
            telemetry.addData("Time", "%.2f/%.2f", t.seconds(), timeoutSec);
            telemetry.update();
            sleep(10);
        }
    }

    public void driveDistanceInches(double inches, double power, double timeoutSec) {
        int delta = inchesToTicks(inches);
        int lf = leftFront.getCurrentPosition() + delta;
        int lb = leftBack.getCurrentPosition() + delta;
        int rf = rightFront.getCurrentPosition() + delta;
        int rb = rightBack.getCurrentPosition() + delta;
        setTargets(lf, lb, rf, rb);
        setDrivePower(Math.abs(power));
        waitForBusyOrTimeout(timeoutSec);
        stopAllDrive();
    }

    public void strafeDistanceInches(double inches, double power, double timeoutSec) {
        int delta = inchesToTicks(inches);
        int lf = leftFront.getCurrentPosition() + delta;
        int lb = leftBack.getCurrentPosition() - delta;
        int rf = rightFront.getCurrentPosition() - delta;
        int rb = rightBack.getCurrentPosition() + delta;
        setTargets(lf, lb, rf, rb);
        setDrivePower(Math.abs(power));
        waitForBusyOrTimeout(timeoutSec);
        stopAllDrive();
    }

    public void turnDegrees(double degrees, double power, double timeoutSec) {
        double turnCirc = Math.PI * (12.0); // simple default track width 12 in; change if needed
        double wheelTravel = turnCirc * (degrees / 360.0);
        int delta = inchesToTicks(wheelTravel);
        int lf = leftFront.getCurrentPosition() - delta;
        int lb = leftBack.getCurrentPosition() - delta;
        int rf = rightFront.getCurrentPosition() + delta;
        int rb = rightBack.getCurrentPosition() + delta;
        setTargets(lf, lb, rf, rb);
        setDrivePower(Math.abs(power));
        waitForBusyOrTimeout(timeoutSec);
        stopAllDrive();
    }

    public void runIntake(double power, double timeoutSec) {
        if (!opModeIsActive()) return;
        intakeMotor.setPower(Math.max(-1.0, Math.min(1.0, power)));
        if (timeoutSec > 0) {
            ElapsedTime t = new ElapsedTime();
            while (opModeIsActive() && t.seconds() < timeoutSec) sleep(10);
            intakeMotor.setPower(0);
        }
    }

    private void setDrivePower(double p) {
        leftFront.setPower(p);
        leftBack.setPower(p);
        rightFront.setPower(p);
        rightBack.setPower(p);
    }

    private void stopAllDrive() {
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}


