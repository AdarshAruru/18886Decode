package org.firstinspires.ftc.teamcode.atlatl_26.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "DT_TeleOpTest", group = "Atlatl")
public class DT_TeleOpTest extends LinearOpMode {

    // ---------------- DRIVE MOTORS ----------------
    private DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    // ---------------- INTAKE / OUTTAKE ----------------
    private DcMotor intake, outtakeTop, outtakeBottom;

    // ---------------- SERVO ----------------
    private Servo myServo;
    private double servoDownPos = 0.0;
    private double servoUpPos = 0.3;
    private boolean servoIsUp = false; // track current servo state

    // ---------------- OUTTAKE VARIABLES ----------------
    private double intakePower = 1;
    private double outtakePowerTop = 0.20;
    private double outtakePowerBottom = 0.55;
    private double powerStep = 0.1;

    // ---------------- BUTTON TRACKERS ----------------
    private boolean prevY = false;
    private boolean prevA = false;
    private boolean prevX = false;

    @Override
    public void runOpMode() throws InterruptedException {
        // ----- DRIVE MOTORS -----
        frontLeftMotor = hardwareMap.get(DcMotor.class, "leftFront");
        backLeftMotor = hardwareMap.get(DcMotor.class, "leftBack");
        frontRightMotor = hardwareMap.get(DcMotor.class, "rightFront");
        backRightMotor = hardwareMap.get(DcMotor.class, "rightBack");

        // Reverse side motors if needed
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        // ----- INTAKE/OUTTAKE -----
        intake = hardwareMap.get(DcMotor.class, "intake");
        outtakeTop = hardwareMap.get(DcMotor.class, "outtakeTop");
        outtakeBottom = hardwareMap.get(DcMotor.class, "outtakeBottom");

        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        outtakeTop.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeBottom.setDirection(DcMotorSimple.Direction.FORWARD);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // ----- SERVO -----
        myServo = hardwareMap.get(Servo.class, "myServo");
        myServo.setPosition(servoDownPos); // start down and stay there during init
        servoIsUp = false;

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // ========== GAMEPAD 1: DRIVE ==========
            double y = -gamepad1.left_stick_y; // forward/back
            double x = gamepad1.left_stick_x * 1.1; // strafe
            double rx = gamepad1.right_stick_x; // turn

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            // ========== GAMEPAD 2: INTAKE/OUTTAKE ==========
            if (gamepad2.left_trigger > 0.1) {
                intake.setPower(intakePower);
                outtakeTop.setPower(outtakePowerTop);
                outtakeBottom.setPower(outtakePowerBottom);
            } else {
                intake.setPower(0);
                outtakeTop.setPower(0);
                outtakeBottom.setPower(0);
            }

            // Adjust outtake power with Y/A on gamepad2
            if (gamepad2.y && !prevY) {
                outtakePowerTop = Math.min(outtakePowerTop + powerStep, 1.0);
                outtakePowerBottom = Math.min(outtakePowerBottom + powerStep, 1.0);
            }
            if (gamepad2.a && !prevA) {
                outtakePowerTop = Math.max(outtakePowerTop - powerStep, 0.0);
                outtakePowerBottom = Math.max(outtakePowerBottom - powerStep, 0.0);
            }

            prevY = gamepad2.y;
            prevA = gamepad2.a;

            // ========== GAMEPAD 2: SERVO TOGGLE ==========
            if (gamepad2.x && !prevX) {
                servoIsUp = !servoIsUp; // toggle the state

                if (servoIsUp) {
                    myServo.setPosition(servoUpPos);
                } else {
                    myServo.setPosition(servoDownPos);
                }
            }

            prevX = gamepad2.x;
        }
    }
}
