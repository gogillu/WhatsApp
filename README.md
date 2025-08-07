# Web Application Bug Testing Tool

This repository contains comprehensive web application bug testing tools designed to identify security vulnerabilities, UI/UX issues, and technical problems in web applications.

## Overview

The tools were originally created to test the web application at `http://27.107.74.204:8090/hob`, but can be used to test any web application for common bugs and security issues.

## Features

### Security Testing
- **XSS (Cross-Site Scripting) Detection**: Tests for reflected XSS vulnerabilities in forms
- **CSRF Protection**: Checks for missing CSRF tokens in POST forms
- **Password Security**: Identifies insecure password handling (GET forms, autocomplete enabled)
- **Authentication Bypass**: Tests API endpoints for missing authentication
- **Information Disclosure**: Detects verbose error messages that reveal system information

### HTML/Syntax Validation
- **HTML Structure**: Validates basic HTML document structure
- **Form Validation**: Checks for proper form attributes and structure
- **Missing Elements**: Identifies missing title tags, alt attributes, etc.

### Accessibility Testing
- **Alt Text**: Checks for missing alt attributes on images
- **Form Labels**: Ensures form inputs have associated labels
- **Heading Structure**: Validates proper heading hierarchy

### API Discovery and Testing
- **Endpoint Discovery**: Finds API endpoints in JavaScript and form actions
- **HTTP Method Testing**: Tests various HTTP methods on discovered endpoints
- **Error Handling**: Validates proper error responses

### UI/UX Analysis
- **Form Usability**: Identifies forms without proper validation
- **Error Handling**: Tests how the application handles invalid input

## Files

### `simple_web_bug_tester.py`
The main testing tool that uses only Python standard library + requests for maximum compatibility.

**Features:**
- Comprehensive HTML parsing using built-in HTMLParser
- Form analysis and testing
- XSS vulnerability detection
- Accessibility auditing
- API endpoint discovery and testing
- Tabular bug reporting

### `web_bug_tester.py`
Enhanced version with additional dependencies (BeautifulSoup, pandas) for more advanced parsing and reporting.

**Additional Features:**
- Better HTML parsing with BeautifulSoup
- Enhanced reporting with pandas DataFrames
- More sophisticated content analysis

## Usage

### Basic Usage

```bash
# Test the target URL
python3 simple_web_bug_tester.py

# Test a custom URL
python3 simple_web_bug_tester.py http://example.com/form
```

### Requirements

For `simple_web_bug_tester.py`:
- Python 3.6+
- requests library (usually pre-installed)

For `web_bug_tester.py`:
- Python 3.6+
- requests
- beautifulsoup4
- pandas
- lxml

Install requirements:
```bash
pip install -r requirements.txt
```

## Sample Output

When testing a web application, the tool generates a comprehensive report:

```
WEB APPLICATION BUG TESTING REPORT
==================================================
Target URL: http://example.com/form
Test Date: 2024-01-01 12:00:00
Total Bugs Found: 5

SEVERITY BREAKDOWN:
--------------------
Critical: 1
High: 2
Medium: 1
Low: 1

CATEGORY BREAKDOWN:
--------------------
Security: 3
HTML: 1
Accessibility: 1

DETAILED BUG REPORT:
--------------------
| #   | Category   | Severity | Priority | Title                    | Description                           | Location      |
|-----|------------|----------|----------|--------------------------|---------------------------------------|---------------|
| 1   | Security   | Critical | Critical | Form 1: XSS Vulnerability| Form is vulnerable to XSS           | /submit       |
| 2   | Security   | High     | High     | Form 1: Missing CSRF    | POST form lacks CSRF protection     | Form #1       |
| 3   | Security   | High     | High     | Password via GET         | Password field in GET form           | Form #2       |
| 4   | HTML       | Medium   | Medium   | Missing Action Attribute | Form lacks action attribute          | Form #1       |
| 5   | Accessibility| Low    | Low      | Missing Alt Text         | Image lacks alt attribute            | /image.jpg    |
```

## Bug Categories

### Security Issues
- **Critical**: XSS vulnerabilities, SQL injection potential
- **High**: Missing CSRF protection, authentication bypass, password security
- **Medium**: Information disclosure, weak validation
- **Low**: Minor security headers, non-critical exposures

### HTML/Technical Issues
- **High**: Invalid HTML structure, broken forms
- **Medium**: Missing required attributes, improper form methods
- **Low**: Missing meta tags, SEO issues

### Accessibility Issues
- **Medium**: Missing form labels, missing alt text
- **Low**: Poor heading structure, missing semantic markup

### UI/UX Issues
- **Medium**: Poor error handling, confusing form behavior
- **Low**: Minor usability improvements

## Testing Methodology

The tool follows this testing sequence:

1. **Connectivity Test**: Verify the target URL is accessible
2. **HTML Analysis**: Parse and validate HTML structure
3. **Form Discovery**: Identify all forms on the page
4. **Security Testing**: Test for XSS, CSRF, and other vulnerabilities
5. **Validation Testing**: Test form submission with invalid data
6. **Accessibility Audit**: Check for accessibility compliance
7. **API Discovery**: Find potential API endpoints
8. **API Testing**: Test discovered endpoints for security issues

## Test Results for http://27.107.74.204:8090/hob

**Status**: Connection Timeout
**Issue**: The target URL is currently unreachable

The tool detected:
- **Category**: Connectivity
- **Severity**: High
- **Priority**: High  
- **Issue**: Server did not respond within 10 seconds
- **Impact**: Unable to perform comprehensive testing

**Recommendations**:
1. Verify the server is running and accessible
2. Check network connectivity
3. Ensure the port 8090 is open and the service is listening
4. Test with a local or accessible URL first

## Extending the Tool

The testing framework is modular and can be extended with additional test cases:

1. **Add new vulnerability tests** in the security testing section
2. **Enhance HTML validation** with more sophisticated checks  
3. **Add performance testing** capabilities
4. **Implement automated reporting** to various formats (JSON, CSV, HTML)

## License

This tool is provided for educational and security testing purposes. Always ensure you have permission to test the target application.