package ru.mirea.kartyshovav.employeedb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etSalary;
    private TextView tvResult;
    private EmployeeDao employeeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etSalary = findViewById(R.id.etSalary);
        tvResult = findViewById(R.id.tvResult);
        Button btnInsert = findViewById(R.id.btnInsert);
        Button btnGetAll = findViewById(R.id.btnGetAll);

        // Получаем методы через App
        AppDatabase db = App.getInstance().getDatabase();
        employeeDao = db.employeeDao();

        btnInsert.setOnClickListener(v -> insertEmployee());
        btnGetAll.setOnClickListener(v -> showAllEmployees());
    }

    private void insertEmployee() {
        String name = etName.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();

        if (name.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        Employee employee = new Employee();
        employee.name = name;
        employee.salary = Integer.parseInt(salaryStr);

        employeeDao.insert(employee);
        Toast.makeText(this, "Сотрудник добавлен", Toast.LENGTH_SHORT).show();

        etName.setText("");
        etSalary.setText("");
    }

    private void showAllEmployees() {
        List<Employee> employees = employeeDao.getAll();

        if (employees.isEmpty()) {
            tvResult.setText("Список сотрудников пуст");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Employee emp : employees) {
            sb.append("ID: ").append(emp.id)
                    .append(" | Имя: ").append(emp.name)
                    .append(" | Зарплата: ").append(emp.salary)
                    .append("\n");
        }
        tvResult.setText(sb.toString());
    }
}