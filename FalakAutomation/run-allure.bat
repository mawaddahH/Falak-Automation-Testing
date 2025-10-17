@echo off
cd /d "%~dp0"
echo Running Allure Report...
allure generate allure-results --clean -o allure-report
allure open allure-report
allure generate --single-file --clean -o falak-report.html allure-results
pause
