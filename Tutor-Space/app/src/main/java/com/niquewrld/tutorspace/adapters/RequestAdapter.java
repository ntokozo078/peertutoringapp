package com.niquewrld.tutorspace.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.TextMessage;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.niquewrld.tutorspace.ChatActivity;
import com.niquewrld.tutorspace.PaymentActivity;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.helpers.RoleHelper;
import com.niquewrld.tutorspace.models.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private final List<Request> requestList;
    private final Context context;
    Request request;

    public RequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        request = requestList.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        RoleHelper ropeHelper = new RoleHelper();
        ropeHelper.isUserTutor(user.getUid(), new RoleHelper.TutorCheckCallback() {
            @Override
            public void onResult(boolean isTutor) {
                // Reset visibility for all views/buttons
                holder.btnPay.setVisibility(View.GONE);
                holder.tvStatus.setVisibility(View.GONE);
                holder.btnChat.setVisibility(View.GONE);
                holder.llConfirmBtn.setVisibility(View.GONE);
                holder.ratingBar.setVisibility(View.GONE);
                holder.btnChooseDate.setVisibility(View.GONE);

                // Set name & subject/message appropriately
                if (isTutor) {
                    holder.tutorName.setText(request.getUserName() + " - " + request.getTutorSubject());
                    holder.tutorSubject.setText(request.getMessage());
                } else {
                    holder.tutorName.setText(request.getTutorName());
                    holder.tutorSubject.setText(request.getTutorSubject());

                    if (request.getTutorDate() == null && request.getDateTimeOptions() != null) {
                        holder.btnChooseDate.setVisibility(View.VISIBLE);
                        holder.btnChooseDate.setOnClickListener(v -> {
                            List<String> dateOptionsList = request.getDateTimeOptions();

                            String[] dateOptions = dateOptionsList.toArray(new String[0]);

                            new AlertDialog.Builder(v.getContext())
                                    .setTitle("Choose a Date")
                                    .setItems(dateOptions, (dialog, which) -> {
                                        String selectedDate = dateOptions[which];

                                        // Save the selected date to Firestore
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection("requests")
                                                .document(request.getId())
                                                .update("tutorDate", selectedDate)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(v.getContext(), "Date saved: " + selectedDate, Toast.LENGTH_SHORT).show();

                                                    // Send a message to the tutor
                                                    String receiverUID = request.getTutorId();
                                                    String messageText = "I have selected " + selectedDate + " as the preferred date for the tutoring session. Looking forward to our session.";
                                                    String receiverType = CometChatConstants.RECEIVER_TYPE_USER;

                                                    TextMessage textMessage = new TextMessage(
                                                            receiverUID,
                                                            messageText,
                                                            receiverType
                                                    );

                                                    CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                                                        @Override
                                                        public void onSuccess(TextMessage message) {
                                                            Log.d("CometChat", "Message sent successfully: " + message.toString());
                                                            holder.btnChooseDate.setVisibility(View.GONE); // Hide the button
                                                            Toast.makeText(v.getContext(), "Message sent to the tutor successfully!", Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onError(CometChatException e) {
                                                            Log.e("CometChat", "Message sending failed with exception: " + e.getMessage());
                                                            Toast.makeText(v.getContext(), "Failed to send message to the tutor.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(v.getContext(), "Failed to save date: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                    .show();
                        });
                    } else {
                        holder.btnChooseDate.setVisibility(View.GONE);
                    }

                }

                holder.timestamp.setText(formatTimestamp(request.getTimestamp()));

                // Payment flow for students
                if (!request.isPayed() && !isTutor) {
                    holder.btnPay.setVisibility(View.VISIBLE);
                    holder.btnPay.setOnClickListener(v -> {
                        Intent intent = new Intent(context, PaymentActivity.class);
                        intent.putExtra("request_id", request.getId());
                        context.startActivity(intent);
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    });
                }

                // Chat button for accepted requests
                if (request.isAccepted() && !request.isDeclined()) {
                    holder.btnChat.setVisibility(View.VISIBLE);
                    holder.btnChat.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("tutorId", request.getTutorId());
                        intent.putExtra("requestId", request.getId());
                        context.startActivity(intent);
                    });
                }


                // Pending/accept/decline flow
                if (!request.isAccepted() && !request.isDeclined()) {
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    holder.tvStatus.setText("Pending");
                    if (isTutor) {
                        holder.llConfirmBtn.setVisibility(View.VISIBLE);
                        holder.btnAccept.setOnClickListener(v -> showDateTimeInputDialog(request.getId()));
                        holder.btnDecline.setOnClickListener(v -> updateRequestStatus(request.getId(), false, true, null));
                    }
                }

                if (request.isDeclined()) {
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    holder.tvStatus.setText("Declined");
                }

                // Rating: show either rating bar (if session has passed by 2 min and unrated) or Rate button (if completed)
                if (!isTutor && request.getTutorDate() != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
                        Date tutorDate = sdf.parse(request.getTutorDate());
                        if (tutorDate != null) {
                            long currentTime = System.currentTimeMillis();
                            long elapsedTime = currentTime - tutorDate.getTime();
                            if (elapsedTime > 2 * 60 * 1000 && request.getRating() == 0) {
                                holder.ratingBar.setVisibility(View.VISIBLE);
                                holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                                    if (fromUser) {
                                        updateRatingInFirestore(request.getId(), rating);
                                        Toast.makeText(context, "Rating submitted: " + rating, Toast.LENGTH_SHORT).show();
                                        holder.ratingBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.e("DateParseError", "Error parsing tutor date: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TutorCheck", "Error checking user role", e);
            }
        });
    }

    private void updateRatingInFirestore(String requestId, float rating) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests").document(requestId)
                .update("rating", rating)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Rating updated successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating rating", e));
    }

    private void showDateTimeInputDialog(String requestId) {
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_date_time_input, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // UI components
        TextView tvCurrentSelection = dialogView.findViewById(R.id.tvCurrentSelection);
        ChipGroup chipGroupSelectedDates = dialogView.findViewById(R.id.chipGroupSelectedDates);
        Button btnPickDate = dialogView.findViewById(R.id.btnPickDate);
        Button btnPickTime = dialogView.findViewById(R.id.btnPickTime);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        AlertDialog dialog = builder.create();
        dialog.show();

        // To track selected dates
        List<String> dateTimeList = new ArrayList<>();
        final String[] currentSelection = {""};

        // Date Picker
        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    new ContextThemeWrapper(context, R.style.DatePickerDialogTheme),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        currentSelection[0] = sdf.format(calendar.getTime());
                        tvCurrentSelection.setText("Currently selecting: " + currentSelection[0]);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Time Picker
        btnPickTime.setOnClickListener(v -> {
            if (currentSelection[0].isEmpty()) {
                Toast.makeText(context, "Please pick a date first", Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    new ContextThemeWrapper(context, R.style.TimePickerDialogTheme),
                    (timeView, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        String time = sdf.format(calendar.getTime());
                        String dateTime = currentSelection[0] + " " + time;
                        if (!dateTimeList.contains(dateTime)) {
                            dateTimeList.add(dateTime);
                            Chip chip = new Chip(context);
                            chip.setText(dateTime);
                            chip.setCloseIconVisible(true);
                            chip.setOnCloseIconClickListener(view -> {
                                chipGroupSelectedDates.removeView(chip);
                                dateTimeList.remove(dateTime);
                                btnPickDate.setEnabled(dateTimeList.size() != 4);
                                btnSubmit.setEnabled(dateTimeList.size() == 4);
                            });
                            chipGroupSelectedDates.addView(chip);
                        }
                        currentSelection[0] = "";
                        tvCurrentSelection.setText("Currently selecting: ");
                        btnSubmit.setEnabled(dateTimeList.size() == 4);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.show();
        });

        // Submit Button
        btnSubmit.setOnClickListener(view -> {
            updateRequestStatus(requestId, true, false, dateTimeList);
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tutorName, tutorSubject, timestamp, tvStatus;
        Button btnPay, btnChat, btnAccept, btnDecline, btnChooseDate;
        LinearLayout llConfirmBtn;
        RatingBar ratingBar;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            btnChooseDate = itemView.findViewById(R.id.btnChooseDate);
            tutorName = itemView.findViewById(R.id.tvTutorName);
            tutorSubject = itemView.findViewById(R.id.tvTutorSubject);
            timestamp = itemView.findViewById(R.id.tvTimestamp);
            btnPay = itemView.findViewById(R.id.btnPay);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            llConfirmBtn = itemView.findViewById(R.id.llConfirmBtn);
        }
    }

    private void updateRequestStatus(String requestId, boolean isAccepted, boolean isDeclined, List<String> dateTimeList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests").document(requestId)
                .update(
                        "isAccepted", isAccepted,
                        "isDeclined", isDeclined,
                        "dateTimeOptions", dateTimeList
                )
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Request status updated successfully");
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("Hello,\n\n");
                    messageBuilder.append("Thank you for your tutoring request. I am pleased to inform you that I have accepted it.\n\n");
                    messageBuilder.append("Here are the dates and times I am available to tutor you. Please review them and let me know which one works best for you:\n\n");
                    for (String dateTime : dateTimeList) {
                        messageBuilder.append("• ").append(dateTime).append("\n");
                    }
                    messageBuilder.append("\nLooking forward to your response.\n\nBest regards,\n" + request.getTutorName());
                    String receiverUID = request.getUserId();
                    String messageText = messageBuilder.toString();
                    String receiverType = CometChatConstants.RECEIVER_TYPE_USER;
                    TextMessage textMessage = new TextMessage(receiverUID, messageText, receiverType);
                    CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                        @Override
                        public void onSuccess(TextMessage message) {
                            Log.d("CometChat", "Message sent successfully: " + message.toString());
                        }
                        @Override
                        public void onError(CometChatException e) {
                            Log.e("CometChat", "Message sending failed with exception: " + e.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating request status", e);
                });
    }
}