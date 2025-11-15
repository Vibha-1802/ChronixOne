# 📅 ChronixOne – Next Date Calculator

ChronixOne is an Android application that calculates the **next date** for any given input between **1872 and 2040**.  
It handles leap years, century years, invalid dates, and displays the day of the week as well.

---

## ⭐ Features

✔ Calculates the **next date** accurately  

✔ Handles **Leap Years** and **Century Years**  

✔ Displays **Day of the Week** 

✔ Supports **EditText input** and **Spinner selection**  

✔ Manual date entry with validation (DD/MM/YYYY)

✔ Shows **error messages** for wrong formats or invalid dates  

✔ Clean UI built

---

## 📱 Mobile Prototype

### 🟣 1. Edge Case Year Transition

Tests the app’s ability to correctly compute next dates for years that are edge cases for year(31/12/1903 -> 01/01/1904).

https://github.com/user-attachments/assets/4cc85435-d0b0-42cf-afa0-75dcbc8d76ad

### 🟣 2. Edge Case Month Transition

Validates the transition from end-of-month to next month (e.g., 31/03 → 01/04).

https://github.com/user-attachments/assets/b433435e-2c6f-4022-b609-28350dc17bfb

### 🟣 3. Invalid Date Handling

Tests invalid user input (01/31/1872)

App displays an error message:

Invalid date entered!

https://github.com/user-attachments/assets/f9743126-3518-4c03-ac59-3893ecb1d1ec

### 🟣 4. Century Year (Not Leap Year)

Tests years divisible by 100 but not by 400 — e.g., 1900.

App displays:

1900 is a Century but not a Leap Year

https://github.com/user-attachments/assets/c882e285-7e1f-4be2-a2e8-d8883be56eb4

### 🟣 5. Leap Year + Century Year

Tests years divisible by 400 such as 2000.

App displays:

2000 is both a Leap Year and a Century

https://github.com/user-attachments/assets/87535a62-131d-47d1-a66e-540cbab6990d

---

## 🧩 Modules

### 1. User Interface Module

Handles user input and displays results.
Includes EditText, Spinner-based input, Buttons, and dynamic TextViews.

### 2. Validation Module

Ensures input is in correct DD/MM/YYYY format and prevents invalid characters.

### 3. Date Calculation Module

Contains all date logic:
getNextDate()
isLeapYear()
daysInMonth()
dayOfWeek()
getYearType()

### 4. Output Module

Displays valid results or error messages dynamically.

---

## 🧠 Algorithms Implemented

### Leap Year Algorithm
If year % 400 == 0 → leap year + century year
Else if year % 100 == 0 → century year
Else if year % 4 == 0 → leap year
Else → not leap year

### Next Date Algorithm
day = day + 1
If day > maxDaysOfMonth → day = 1, month++
If month > 12 → month = 1, year++

### Year Type Algorithm

Determines if the year is:
Leap Year
Century Year
Both
Neither

---

## 🛠 Technologies Used

| Component  | Technology                         |
| ---------- | ---------------------------------- |
| Language   | Java                               |
| IDE        | Android Studio                     |
| UI Design  | XML (ConstraintLayout)             |
| Testing    | Android Emulator / Physical Device |
| OS Support | Android 7.0+                       |

---
