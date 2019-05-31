package com.ordermanagementapp.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ordermanagementapp.adapter.OrderListAdapter;
import com.ordermanagementapp.model.OrderModel;
import com.ordermanagementapp.interfaces.OrderUpdateInterface;
import com.ordermanagementapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OrderUpdateInterface, LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    Context context;
    RecyclerView rvList;
    TextView tvNoOrders, tvNewOrder;
    ProgressDialog progressDialog;
    OrderListAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<OrderModel> orderModelArrayList;
    DatabaseReference myRef;

    String cityName = "Hyderabad", stateName = "Telangana";
    private LocationManager locationManager;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        rvList = findViewById(R.id.rvList);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        tvNewOrder = findViewById(R.id.tvNewOrder);
        orderModelArrayList = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(context);

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(linearLayoutManager);

        adapter = new OrderListAdapter(context, orderModelArrayList, this);
        rvList.setAdapter(adapter);


        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            cityName = "";
            stateName = "";
        }


        tvNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage("Loading data");
            progressDialog.show();
        }

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                getAllTask(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                getAllTask(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                taskDeletion(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAllTask(DataSnapshot dataSnapshot) {
        OrderModel value = dataSnapshot.getValue(OrderModel.class);
        Log.e("valuee", value + "");
        if (value != null) {
            String orderNumber = value.getOrderNumber();
            String orderDueDate = value.getOrderDueDate();
            String customerName = value.getCustomerName();
            String customerAddress = value.getCustomerAddress();
            String customerPhone = value.getCustomerPhone();
            String orderTotal = value.getOrderTotal();
            String location = value.getLocation();

            orderModelArrayList.add(new OrderModel(orderNumber, orderDueDate, customerName, customerPhone, customerAddress, orderTotal, location));
            adapter.updateList(orderModelArrayList);
        }
        if (orderModelArrayList.size() > 0) {
            rvList.setVisibility(View.VISIBLE);
            tvNoOrders.setVisibility(View.GONE);
        } else {
            rvList.setVisibility(View.GONE);
            tvNoOrders.setVisibility(View.VISIBLE);
        }
        progressDialog.dismiss();
    }

    private void taskDeletion(DataSnapshot dataSnapshot) {
        OrderModel taskTitle = dataSnapshot.getValue(OrderModel.class);
        for (int i = 0; i < orderModelArrayList.size(); i++) {
            if (taskTitle != null && orderModelArrayList.get(i).getOrderNumber().equals(taskTitle.getOrderNumber())) {
                orderModelArrayList.remove(i);
                adapter.updateList(orderModelArrayList);
                if (orderModelArrayList.size() == 0) {
                    rvList.setVisibility(View.GONE);
                    tvNoOrders.setVisibility(View.VISIBLE);
                } else {
                    rvList.setVisibility(View.VISIBLE);
                    tvNoOrders.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void edit(String orderNo, String orderDueDate, String customerName, String customerPhone, String customerAddress,
                     String orderTotal, String location) {
        showEditOrderDialog(orderNo, orderDueDate, customerName, customerPhone, customerAddress, orderTotal, location);
    }

    @Override
    public void delete(final String orderNo) {

        new AlertDialog.Builder(context).setTitle("Delete!").setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.orderByChild("orderNumber").equalTo(orderNo);
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    public void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_order_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText etOrderNo = dialog.findViewById(R.id.etOrderNo);
        final EditText etDueDate = dialog.findViewById(R.id.etDueDate);
        final EditText etName = dialog.findViewById(R.id.etName);
        final EditText etPhone = dialog.findViewById(R.id.etPhone);
        final EditText etAddress = dialog.findViewById(R.id.etAddress);
        final EditText etTotal = dialog.findViewById(R.id.etTotal);
        Button btSubmit = dialog.findViewById(R.id.btSubmit);
        ImageView ivClose = dialog.findViewById(R.id.ivClose);
        final Calendar myCalendar = Calendar.getInstance();

        dialog.show();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etDueDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Define the criteria how to select the locatioin provider -> use
                // default
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    System.out.println("Provider " + provider + " has been selected.");
                    onLocationChanged(location);
                } else {
                    cityName = "";
                    stateName = "";
                }

                if (etOrderNo.getText().toString().trim().isEmpty()) {
                    etOrderNo.setError("Enter order no");
                } else if (etDueDate.getText().toString().trim().isEmpty()) {
                    etDueDate.setError("Enter due date");
                } else if (etName.getText().toString().trim().isEmpty()) {
                    etName.setError("Enter name");
                } else if (etPhone.getText().toString().trim().isEmpty()) {
                    etPhone.setError("Enter phone");
                } else if (etPhone.getText().toString().trim().length() < 10) {
                    etPhone.setError("Enter correct phone no");
                } else if (etAddress.getText().toString().trim().isEmpty()) {
                    etAddress.setError("Enter address");
                } else if (etTotal.getText().toString().trim().isEmpty()) {
                    etTotal.setError("Enter total");
                } else {
                    dialog.dismiss();
                    OrderModel orderModel = new OrderModel(etOrderNo.getText().toString().trim(),
                            etDueDate.getText().toString().trim(), etName.getText().toString().trim(),
                            etPhone.getText().toString().trim(), etAddress.getText().toString().trim(),
                            etTotal.getText().toString().trim(), cityName + ", " + stateName);
                    myRef.push().setValue(orderModel);
                }
            }
        });

    }

    public void showEditOrderDialog(String orderNo, String orderDueDate, String customerName, String customerPhone, String customerAddress,
                                    String orderTotal, String location) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_order_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tvOrderNo = dialog.findViewById(R.id.tvOrderNo);
        final EditText etDueDate = dialog.findViewById(R.id.etDueDate);
        final EditText etName = dialog.findViewById(R.id.etName);
        final EditText etPhone = dialog.findViewById(R.id.etPhone);
        final EditText etAddress = dialog.findViewById(R.id.etAddress);
        final EditText etTotal = dialog.findViewById(R.id.etTotal);
        final TextView tvLocation = dialog.findViewById(R.id.tvLocation);
        Button btSubmit = dialog.findViewById(R.id.btSubmit);
        ImageView ivClose = dialog.findViewById(R.id.ivClose);
        final Calendar myCalendar = Calendar.getInstance();

        dialog.show();

        tvOrderNo.setText(orderNo);
        etDueDate.setText(orderDueDate);
        etName.setText(customerName);
        etPhone.setText(customerPhone);
        etAddress.setText(customerAddress);
        etTotal.setText(orderTotal);
        tvLocation.setText(location);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etDueDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Define the criteria how to select the locatioin provider -> use
                // default
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    System.out.println("Provider " + provider + " has been selected.");
                    onLocationChanged(location);
                } else {
                    cityName = "";
                    stateName = "";
                }

                if (etDueDate.getText().toString().trim().isEmpty()) {
                    etDueDate.setError("Enter due date");
                } else if (etName.getText().toString().trim().isEmpty()) {
                    etName.setError("Enter name");
                } else if (etPhone.getText().toString().trim().isEmpty()) {
                    etPhone.setError("Enter phone");
                } else if (etPhone.getText().toString().trim().length() < 10) {
                    etPhone.setError("Enter correct phone no");
                } else if (etAddress.getText().toString().trim().isEmpty()) {
                    etAddress.setError("Enter address");
                } else if (etTotal.getText().toString().trim().isEmpty()) {
                    etTotal.setError("Enter total");
                } else {
                    dialog.dismiss();
                    final OrderModel orderModel = new OrderModel(tvOrderNo.getText().toString().trim(),
                            etDueDate.getText().toString().trim(), etName.getText().toString().trim(),
                            etPhone.getText().toString().trim(), etAddress.getText().toString().trim(),
                            etTotal.getText().toString().trim(), cityName + ", " + stateName);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query applesQuery = ref.orderByChild("orderNumber").equalTo(tvOrderNo.getText().toString().trim());
                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                appleSnapshot.getRef().removeValue();
                                appleSnapshot.getRef().setValue(orderModel);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });

                }
            }
        });

    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        exitAppMethod();
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void exitAppMethod() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }
}
