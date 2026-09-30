package com.example.mdp_group_14;

import static com.example.mdp_group_14.Home.refreshMessageReceivedNS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.Arrays;

public class ControlFragment extends Fragment {
    private static final String TAG = "ControlFragment";

    SharedPreferences sharedPreferences;

    // Control Button
    ImageButton moveForwardImageBtn, turnRightImageBtn, moveBackImageBtn, turnLeftImageBtn,turnbleftImageBtn,turnbrightImageBtn;
    ImageButton exploreResetButton, fastestResetButton;
    private static long exploreTimer, fastestTimer;
    public static ToggleButton exploreButton, fastestButton;
    public static TextView exploreTimeTextView, fastestTimeTextView, robotStatusTextView;
    private static GridMap gridMap;

    // Timer
    public static Handler timerHandler = new Handler();

    Button sendObstaclesButton;
    Button explorePauseButton, fastestPauseButton, practiceTasksButton;
    private long exploreElapsed, fastestElapsed, practiceStarted, practiceElapsed;
    private boolean explorePaused, fastestPaused, practiceRunning, practicePaused;
    private Handler practiceHandler = new Handler();
    private TextView practiceTimerView;
    private LinearLayout practiceLapseList;

    public static Runnable timerRunnableExplore = new Runnable() {
        @Override
        public void run() {
            long millisExplore = System.currentTimeMillis() - exploreTimer;
            int secondsExplore = (int) (millisExplore / 1000);
            int minutesExplore = secondsExplore / 60;
            secondsExplore = secondsExplore % 60;

            if (!Home.stopTimerFlag) {
                exploreTimeTextView.setText(String.format("%02d:%02d", minutesExplore,
                        secondsExplore));
                timerHandler.postDelayed(this, 500);
            }
        }
    };

    public static Runnable timerRunnableFastest = new Runnable() {
        @Override
        public void run() {
            long millisFastest = System.currentTimeMillis() - fastestTimer;
            int secondsFastest = (int) (millisFastest / 1000);
            int minutesFastest = secondsFastest / 60;
            secondsFastest = secondsFastest % 60;

            if (!Home.stopWk9TimerFlag) {
                fastestTimeTextView.setText(String.format("%02d:%02d", minutesFastest,
                        secondsFastest));
                timerHandler.postDelayed(this, 500);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate
        View root = inflater.inflate(R.layout.controls, container, false);

        // get shared preferences
        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences",
                Context.MODE_PRIVATE);

        // variable initialization
        moveForwardImageBtn = Home.getUpBtn();
        turnRightImageBtn = Home.getRightBtn();
        moveBackImageBtn = Home.getDownBtn();
        turnLeftImageBtn = Home.getLeftBtn();
        turnbleftImageBtn = Home.getbLeftBtn();
        turnbrightImageBtn = Home.getbRightBtn();
        exploreTimeTextView = root.findViewById(R.id.exploreTimeTextView2);
        fastestTimeTextView = root.findViewById(R.id.fastestTimeTextView2);
        exploreButton = root.findViewById(R.id.exploreToggleBtn2);
        sendObstaclesButton = root.findViewById(R.id.sendObstaclesButton);
        fastestButton = root.findViewById(R.id.fastestToggleBtn2);
        exploreResetButton = root.findViewById(R.id.exploreResetImageBtn2);
        fastestResetButton = root.findViewById(R.id.fastestResetImageBtn2);
        explorePauseButton = root.findViewById(R.id.explorePauseBtn);
        fastestPauseButton = root.findViewById(R.id.fastestPauseBtn);
        practiceTasksButton = root.findViewById(R.id.practiceTasksBtn);
        robotStatusTextView = Home.getRobotStatusTextView();
        fastestTimer = 0;
        exploreTimer = 0;
        exploreElapsed = fastestElapsed = 0;
        //startSend = root.findViewById(R.id.startSend); //just added, need to test

        gridMap = Home.getGridMap();

        // Button Listener
        moveForwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked moveForwardImageBtn");
                if (gridMap.getCanDrawRobot()) {
                    gridMap.moveRobot("forward");
                    Home.refreshLabel();    // update x and y coordinate displayed
                    // display different statuses depending on validity of robot action
                    if (gridMap.getValidPosition()){
                        updateStatus("moving forward");}
                    else {
                        updateStatus("Unable to move forward");
                    }

                    Home.printMessage("f");
                }
                else
                    updateStatus("Please press 'SET START POINT'");
                showLog("Exiting moveForwardImageBtn");
            }
        });

        turnRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked turnRightImageBtn");
                if (gridMap.getCanDrawRobot()) {
                    gridMap.moveRobot("right");
                    Home.refreshLabel();
                    Home.printMessage("fr");
//                    showLog("test");
                    System.out.println(Arrays.toString(gridMap.getCurCoord()));
                }
                else
                    updateStatus("Please press 'SET START POINT'");
                showLog("Exiting turnRightImageBtn");
            }
        });
        turnbrightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked turnbRightImageBtn");
                if (gridMap.getCanDrawRobot()) {
                    gridMap.moveRobot("backright");
                    Home.refreshLabel();
                    Home.printMessage("br");
                    System.out.println(Arrays.toString(gridMap.getCurCoord()));
                }
                else
                    updateStatus("Please press 'SET START POINT'");
                showLog("Exiting turnbRightImageBtn");
            }
        });

        moveBackImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked moveBackwardImageBtn");
                if (gridMap.getCanDrawRobot()) {
                    gridMap.moveRobot("back");
                    Home.refreshLabel();
                    if (gridMap.getValidPosition())
                        updateStatus("moving backward");
                    else
                        updateStatus("Unable to move backward");
                    Home.printMessage("b");
                }
                else
                    updateStatus("Please press 'SET START POINT'");
                showLog("Exiting moveBackwardImageBtn");
            }
        });

        turnLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked turnLeftImageBtn");
                if (gridMap.getCanDrawRobot()) {
                    gridMap.moveRobot("left");
                    Home.refreshLabel();
                    updateStatus("turning left");
                    Home.printMessage("fl");
                }
                else
                    updateStatus("Please press 'SET START POINT'");
                showLog("Exiting turnLeftImageBtn");
            }
        });
        turnbleftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked turnbLeftImageBtn");
                if (gridMap.getCanDrawRobot()) {
                    gridMap.moveRobot("backleft");
                    Home.refreshLabel();
                    updateStatus("turning left");
                    Home.printMessage("bl");
                }
                else
                    updateStatus("Please press 'SET START POINT'");
                showLog("Exiting turnbLeftImageBtn");
            }
        });

        // Obstacle setup is separate from BEGIN. Planning is asynchronous, so the user can
        // wait for STATUS:Ready before starting the run.
        sendObstaclesButton.setOnClickListener(view -> {
            for (String line : gridMap.getObstacleLines()) {
                Home.printMessage(line);
                showLog("Obstacle setup complete. line: " + line);
            }
            Home.printMessage("DONE");

            robotStatusTextView.setText("Planning");
        });

        // Start Task 1 challenge
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked Task 1 Btn (exploreToggleBtn)");
                ToggleButton exploreToggleBtn = (ToggleButton) v;

                if (exploreToggleBtn.getText().equals("TASK 1 START")) {
                    showToast("Task 1 timer stop!");
                    exploreElapsed = System.currentTimeMillis() - exploreTimer;
                    robotStatusTextView.setText("Task 1 Stopped");
                    timerHandler.removeCallbacks(timerRunnableExplore);
                    Home.printMessage("STOP"); //send a string "STOP" to the robot
                }
                else if (exploreToggleBtn.getText().equals("STOP")) {
                    if (!Home.isRobotReady()) {
                        showToast("Wait for STATUS: Ready after reset and planning");
                        exploreToggleBtn.setChecked(false);
                        return;
                    }
                    Home.printMessage("BEGIN"); //send a string "BEGIN" to the RPI
                    // Start timer
                    Home.stopTimerFlag = false;
                    showToast("Task 1 timer start!");

                    robotStatusTextView.setText("Task 1 Started");
                    exploreTimer = System.currentTimeMillis() - exploreElapsed;
                    explorePaused = false;
                    timerHandler.postDelayed(timerRunnableExplore, 0);
                }
                else {
                    showToast("Else statement: " + exploreToggleBtn.getText());
                }
                showLog("Exiting exploreToggleBtn");
            }
        });

        explorePauseButton.setOnClickListener(v -> {
            if (!explorePaused) {
                exploreElapsed = System.currentTimeMillis() - exploreTimer;
                Home.stopTimerFlag = true;
                timerHandler.removeCallbacks(timerRunnableExplore);
                explorePauseButton.setText("RESUME");
            } else {
                exploreTimer = System.currentTimeMillis() - exploreElapsed;
                Home.stopTimerFlag = false;
                timerHandler.post(timerRunnableExplore);
                explorePauseButton.setText("PAUSE");
            }
            explorePaused = !explorePaused;
        });


        //Start Task 2 Challenge Timer
        fastestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked Task 2 Btn (fastestToggleBtn)");
                ToggleButton fastestToggleBtn = (ToggleButton) v;
                if (fastestToggleBtn.getText().equals("TASK 2 START")) {
                    showToast("Task 2 timer stop!");
                    fastestElapsed = System.currentTimeMillis() - fastestTimer;
                    robotStatusTextView.setText("Task 2 Stopped");
                    timerHandler.removeCallbacks(timerRunnableFastest);
                    Home.printMessage("STOP"); //send a string "STOP" to the robot
                }
                else if (fastestToggleBtn.getText().equals("STOP")) {
                    showToast("Task 2 timer start!");
                    Home.printMessage("BEGIN"); //send a string "BEGIN" to the RPI
                    Home.stopWk9TimerFlag = false;
                    robotStatusTextView.setText("Task 2 Started");
                    fastestTimer = System.currentTimeMillis() - fastestElapsed;
                    fastestPaused = false;
                    timerHandler.postDelayed(timerRunnableFastest, 0);
                }
                else
                    showToast(fastestToggleBtn.getText().toString());
                showLog("Exiting fastestToggleBtn");
            }
        });

        fastestPauseButton.setOnClickListener(v -> {
            if (!fastestPaused) {
                fastestElapsed = System.currentTimeMillis() - fastestTimer;
                Home.stopWk9TimerFlag = true;
                timerHandler.removeCallbacks(timerRunnableFastest);
                fastestPauseButton.setText("RESUME");
            } else {
                fastestTimer = System.currentTimeMillis() - fastestElapsed;
                Home.stopWk9TimerFlag = false;
                timerHandler.post(timerRunnableFastest);
                fastestPauseButton.setText("PAUSE");
            }
            fastestPaused = !fastestPaused;
        });

        practiceTasksButton.setOnClickListener(v -> showPracticeTasks());

        exploreResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked exploreResetImageBtn");
                showToast("Resetting exploration time...");
                exploreTimeTextView.setText("00:00");
                exploreElapsed = 0; explorePaused = false; explorePauseButton.setText("PAUSE");
                robotStatusTextView.setText("Not Available");
                if(exploreButton.isChecked())
                    exploreButton.toggle();
                timerHandler.removeCallbacks(timerRunnableExplore);
                showLog("Exiting exploreResetImageBtn");
            }
        });

        fastestResetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showLog("Clicked fastestResetImgBtn");
                showToast("Resetting Fastest Time...");
                fastestTimeTextView.setText("00:00");
                fastestElapsed = 0; fastestPaused = false; fastestPauseButton.setText("PAUSE");
                robotStatusTextView.setText("Fastest Car Finished");
                if(fastestButton.isChecked()){
                    fastestButton.toggle();
                }
                timerHandler.removeCallbacks(timerRunnableFastest);
                showLog("Exiting fastestResetImgBtn");
            }
        });

        /*
        startSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showLog("Clicked startSendBtn");
                showToast("Sending BEGIN to robot...");
                exploreButton.toggle();
                if (exploreButton.getText().equals("WK8 START")) {
                    showToast("Auto Movement/ImageRecog timer stop!");
                    robotStatusTextView.setText("Auto Movement Stopped");
                    timerHandler.removeCallbacks(timerRunnableExplore);
                }
                else if (exploreButton.getText().equals("STOP")) {
                    // Get String value that represents obstacle configuration
                    String msg = gridMap.getObstacles();
                    // Send this String over via BT
                    //Home.printCoords(msg);
                    // Start timer
                    Home.stopTimerFlag = false;
                    showToast("Auto Movement/ImageRecog timer start!");

                    robotStatusTextView.setText("Auto Movement Started");
                    exploreTimer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnableExplore, 0);
                }
                //ok
                Home.printMessage("BEGIN"); //send a string "BEGIN" to the RPI
                showLog("Exiting startSend");
            }
        });
         */

        return root;
    }

    private void showPracticeTasks() {
        LinearLayout panel = new LinearLayout(requireContext());
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(24, 8, 24, 8);
        practiceTimerView = new TextView(requireContext());
        practiceTimerView.setText("00:00");
        practiceTimerView.setTextSize(28);
        practiceTimerView.setTextColor(getResources().getColor(R.color.colorBlack));
        practiceTimerView.setGravity(Gravity.CENTER);
        panel.addView(practiceTimerView);
        LinearLayout buttons = new LinearLayout(requireContext());
        Button start = new Button(requireContext()); start.setText("START");
        Button pause = new Button(requireContext()); pause.setText("PAUSE");
        Button stop = new Button(requireContext()); stop.setText("STOP");
        Button lapse = new Button(requireContext()); lapse.setText("LAPSE");
        buttons.addView(start); buttons.addView(pause); buttons.addView(stop); buttons.addView(lapse);
        panel.addView(buttons);
        practiceLapseList = new LinearLayout(requireContext());
        practiceLapseList.setOrientation(LinearLayout.VERTICAL);
        panel.addView(practiceLapseList);
        panel.setBackgroundColor(getResources().getColor(R.color.lighterYellow));
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setTitle("Practice Tasks").setView(panel).setNegativeButton("CLOSE", null).create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorYellow));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(14);
            dialog.getWindow().setBackgroundDrawableResource(R.color.lighterYellow);
        });
        Runnable tick = new Runnable() { public void run() { if (practiceRunning && !practicePaused) { long ms = System.currentTimeMillis() - practiceStarted; practiceTimerView.setText(formatTime(ms)); practiceHandler.postDelayed(this, 250); } } };
        start.setOnClickListener(v -> { if (!practiceRunning) { practiceRunning = true; practicePaused = false; practiceStarted = System.currentTimeMillis() - practiceElapsed; practiceHandler.post(tick); } else if (practicePaused) { practicePaused = false; practiceStarted = System.currentTimeMillis() - practiceElapsed; practiceHandler.post(tick); } });
        pause.setOnClickListener(v -> { if (practiceRunning && !practicePaused) { practiceElapsed = System.currentTimeMillis() - practiceStarted; practicePaused = true; pause.setText("RESUME"); } else if (practicePaused) { practicePaused = false; practiceStarted = System.currentTimeMillis() - practiceElapsed; pause.setText("PAUSE"); practiceHandler.post(tick); } });
        stop.setOnClickListener(v -> { if (practiceRunning) practiceElapsed = System.currentTimeMillis() - practiceStarted; practiceRunning = false; practicePaused = false; practiceTimerView.setText(formatTime(practiceElapsed)); });
        lapse.setOnClickListener(v -> { if (!practiceRunning) return; long elapsed = System.currentTimeMillis() - practiceStarted; EditText description = new EditText(requireContext()); description.setHint("Description"); description.setSingleLine(); description.setTextColor(getResources().getColor(R.color.colorBlack)); LinearLayout item = new LinearLayout(requireContext()); item.setOrientation(LinearLayout.HORIZONTAL); TextView time = new TextView(requireContext()); time.setText(formatTime(elapsed) + "  "); time.setTextColor(getResources().getColor(R.color.colorBlack)); item.addView(time); item.addView(description, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); practiceLapseList.addView(item); });
        dialog.setOnDismissListener(d -> { practiceRunning = false; practiceHandler.removeCallbacks(tick); });
        dialog.show();
    }

    private String formatTime(long millis) { return String.format("%02d:%02d", (millis / 60000), (millis / 1000) % 60); }



    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void updateStatus(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0, 0);
        toast.show();
    }
}
