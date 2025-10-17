# Falak-Automation-Testing
Automation testing project for Falak linguistic platform using Selenium, TestNG, Allure, and Rest Assured.

# 🧪 Falak QA Automation Project  

## 🌍 Overview  
This repository presents a **Quality Assurance Automation Framework** designed for the **Falak Linguistic Platform**.  
It focuses on ensuring the platform’s reliability and accuracy through both **UI and API automated testing**, integrating modern tools such as **Java**, **Selenium**, **TestNG**, **Allure**, and **Rest Assured**.  

---

## ⚙️ Tech Stack  
| Category | Tools & Technologies |
|-----------|----------------------|
| **Language** | Java |
| **Frameworks** | Selenium WebDriver, TestNG |
| **Reporting** | Allure Reports |
| **API Testing** | Rest Assured |
| **Build Tool** | Maven |

---

## ✨ Key Features  
- 🧩 **Page Object Model (POM)** structure for maintainable and scalable tests.  
- 🔁 **TestNG Suite Execution** with retry logic for failed test cases.  
- 📸 **Automatic Screenshots** captured upon test failure.  
- 📊 **Interactive Allure Reports** for detailed and visually appealing test summaries.  
- 🌐 **API Validation** using Rest Assured to ensure backend and UI data consistency.  

---

## 🧪 Tested Components  
The automated test coverage includes the following core tools and pages of the Falak platform:  
- **Concordancer Tool (أداة الكشاف السياقي)**  
- **N-grams Tool (أداة التتابعات اللفظية)**  
- **Home Page**

---

## ▶️ How to Run  

### From IDE  
Run any test class directly, or execute the full suite via:  
```bash
testng.xml
```

### Generate Allure Report  
To generate and view a complete interactive report:  
```bash
allure generate allure-results --clean -o allure-report
allure open allure-report
```

To generate a single portable HTML report:  
```bash
allure generate --single-file allure-results
```
