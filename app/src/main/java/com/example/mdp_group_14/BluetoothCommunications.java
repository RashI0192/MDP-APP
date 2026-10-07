package com.example.mdp_group_14;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BluetoothCommunications extends Fragment {
    private static final String TAG = "BluetoothComms";

    private SharedPreferences sharedPreferences;
    private TextView messageReceivedTextView;
    private EditText typeBoxEditText;

    public static void updateMessageLog(Context context, String message) {
        SharedPreferences prefs = context.getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        String previous = prefs.getString("message", "");
        String next = previous + (previous.isEmpty() ? "" : "\n") + message;
        // Bound diagnostic storage so a day-long run cannot exhaust preferences.
        if (next.length() > 32768) next = next.substring(next.length() - 32768);
        prefs.edit().putString("message", next).apply();
        Intent intent = new Intent("uiMessage");
        intent.putExtra("receivedMessage", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("incomingMessage");
        filter.addAction("uiMessage");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mReceiver, filter);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_communications, container, false);
        bindMovementControls(root);


        ImageButton send;
        send = root.findViewById(R.id.messageButton);

        // Message Box
        messageReceivedTextView = root.findViewById(R.id.messageReceivedTitleTextView);
        messageReceivedTextView.setMovementMethod(new ScrollingMovementMethod());
        typeBoxEditText = root.findViewById(R.id.typeBoxEditText);

        // get shared preferences
        sharedPreferences = requireActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);

        String savedMessage = sharedPreferences.getString("message", "");
        if (!savedMessage.isEmpty()) {
            messageReceivedTextView.setText(savedMessage);
            messageReceivedTextView.post(() ->
                    messageReceivedTextView.scrollTo(0, messageReceivedTextView.getBottom()));
        }

        send.setOnClickListener(view -> {
            showLog("Clicked sendTextBtn");
            String sentText = typeBoxEditText.getText().toString();

            updateMessageLog(requireContext(), sentText);
            typeBoxEditText.setText("");

            Home.printMessage(sentText);
            showLog("Exiting sendTextBtn");
        });

        return root;
    }

    private void bindMovementControls(View root) {
        ImageButton left = root.findViewById(R.id.leftBtn);
        ImageButton forward = root.findViewById(R.id.upBtn);
        ImageButton right = root.findViewById(R.id.rightBtn);
        ImageButton backLeft = root.findViewById(R.id.bleftBtn);
        ImageButton back = root.findViewById(R.id.downBtn);
        ImageButton backRight = root.findViewById(R.id.brightBtn);

        left.setOnClickListener(v -> moveRobot("left", "fl"));
        forward.setOnClickListener(v -> moveRobot("forward", "f"));
        right.setOnClickListener(v -> moveRobot("right", "fr"));
        backLeft.setOnClickListener(v -> moveRobot("backleft", "bl"));
        back.setOnClickListener(v -> moveRobot("back", "b"));
        backRight.setOnClickListener(v -> moveRobot("backright", "br"));
    }

    private void moveRobot(String direction, String command) {
        GridMap map = Home.getGridMap();
        if (map != null && map.getCanDrawRobot()) {
            map.moveRobot(direction);
            Home.refreshLabel();
            Home.printMessage(command);
        } else if (Home.getRobotStatusTextView() != null) {
            Home.getRobotStatusTextView().setText("Please press 'SET START POINT'");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mReceiver);
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("receivedMessage");
            if (messageReceivedTextView != null) {
                messageReceivedTextView.append(text + "\n");
            }
        }
    };
}
