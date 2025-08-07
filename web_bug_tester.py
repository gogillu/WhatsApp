#!/usr/bin/env python3
"""
Web Application Bug Testing Tool
Comprehensive testing script for finding bugs in web applications
Designed for testing http://27.107.74.204:8090/hob
"""

import requests
import re
import json
import urllib.parse
from bs4 import BeautifulSoup
from datetime import datetime
import html
import sys
from typing import List, Dict, Any
import pandas as pd


class WebBugTester:
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip('/')
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'WebBugTester/1.0 (Security Testing Tool)'
        })
        self.bugs = []
        self.forms = []
        self.apis = []
        
    def log_bug(self, category: str, severity: str, priority: str, 
                title: str, description: str, location: str = ""):
        """Log a discovered bug"""
        bug = {
            'Category': category,
            'Severity': severity,
            'Priority': priority,
            'Title': title,
            'Description': description,
            'Location': location,
            'Timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }
        self.bugs.append(bug)
        
    def test_connectivity(self) -> bool:
        """Test basic connectivity to the target URL"""
        try:
            response = self.session.get(self.base_url, timeout=10)
            if response.status_code == 200:
                print(f"✓ Successfully connected to {self.base_url}")
                return True
            else:
                self.log_bug(
                    "Connectivity", "High", "High",
                    f"HTTP Error {response.status_code}",
                    f"Server returned status code {response.status_code}",
                    self.base_url
                )
                return False
        except requests.exceptions.Timeout:
            self.log_bug(
                "Connectivity", "High", "High",
                "Connection Timeout",
                "Server did not respond within 10 seconds",
                self.base_url
            )
            return False
        except requests.exceptions.ConnectionError:
            self.log_bug(
                "Connectivity", "High", "High",
                "Connection Error",
                "Unable to establish connection to server",
                self.base_url
            )
            return False
        except Exception as e:
            self.log_bug(
                "Connectivity", "High", "High",
                "Unknown Connection Error",
                f"Error: {str(e)}",
                self.base_url
            )
            return False
    
    def fetch_and_parse_html(self) -> BeautifulSoup:
        """Fetch HTML content and parse it"""
        try:
            response = self.session.get(self.base_url, timeout=10)
            response.raise_for_status()
            
            # Check for HTML syntax issues
            if not response.text.strip():
                self.log_bug(
                    "HTML", "Medium", "Medium",
                    "Empty Response Body",
                    "Server returned empty content",
                    self.base_url
                )
            
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # Check for basic HTML structure
            if not soup.find('html'):
                self.log_bug(
                    "HTML", "Medium", "Medium",
                    "Missing HTML Tag",
                    "Document lacks proper HTML tag",
                    self.base_url
                )
            
            if not soup.find('head'):
                self.log_bug(
                    "HTML", "Low", "Low",
                    "Missing HEAD Section",
                    "Document lacks HEAD section",
                    self.base_url
                )
            
            if not soup.find('title'):
                self.log_bug(
                    "SEO", "Low", "Low",
                    "Missing Title Tag",
                    "Document lacks title tag for SEO",
                    self.base_url
                )
            
            return soup
            
        except Exception as e:
            self.log_bug(
                "HTML", "High", "High",
                "HTML Parsing Error",
                f"Failed to parse HTML: {str(e)}",
                self.base_url
            )
            return None
    
    def analyze_forms(self, soup: BeautifulSoup):
        """Analyze forms for potential issues"""
        forms = soup.find_all('form')
        
        if not forms:
            self.log_bug(
                "UI/UX", "Low", "Low",
                "No Forms Found",
                "No forms detected on the page",
                self.base_url
            )
            return
        
        for i, form in enumerate(forms):
            form_info = {
                'index': i,
                'action': form.get('action', ''),
                'method': form.get('method', 'GET').upper(),
                'inputs': []
            }
            
            # Check form action
            if not form.get('action'):
                self.log_bug(
                    "Security", "Medium", "Medium",
                    f"Form {i+1}: Missing Action Attribute",
                    "Form lacks action attribute, may cause unexpected behavior",
                    f"Form #{i+1}"
                )
            
            # Check form method
            if form_info['method'] not in ['GET', 'POST']:
                self.log_bug(
                    "HTML", "Medium", "Medium",
                    f"Form {i+1}: Invalid Method",
                    f"Form method '{form_info['method']}' is not standard",
                    f"Form #{i+1}"
                )
            
            # Analyze form inputs
            inputs = form.find_all(['input', 'textarea', 'select'])
            for input_elem in inputs:
                input_info = {
                    'type': input_elem.get('type', 'text'),
                    'name': input_elem.get('name', ''),
                    'id': input_elem.get('id', ''),
                    'required': input_elem.has_attr('required'),
                    'placeholder': input_elem.get('placeholder', '')
                }
                form_info['inputs'].append(input_info)
                
                # Check for missing name attribute
                if not input_info['name'] and input_info['type'] not in ['button', 'submit']:
                    self.log_bug(
                        "HTML", "Medium", "Medium",
                        f"Form {i+1}: Input Missing Name",
                        "Input element lacks name attribute for form submission",
                        f"Form #{i+1}"
                    )
                
                # Check for password fields without proper security
                if input_info['type'] == 'password':
                    if form_info['method'] == 'GET':
                        self.log_bug(
                            "Security", "High", "High",
                            f"Form {i+1}: Password via GET",
                            "Password field in form using GET method (security risk)",
                            f"Form #{i+1}"
                        )
                    
                    # Check for autocomplete on password fields
                    if input_elem.get('autocomplete') != 'off':
                        self.log_bug(
                            "Security", "Medium", "Medium",
                            f"Form {i+1}: Password Autocomplete",
                            "Password field allows autocomplete (potential security risk)",
                            f"Form #{i+1}"
                        )
            
            # Check for CSRF protection
            csrf_token = form.find('input', {'name': re.compile(r'csrf|token', re.I)})
            if form_info['method'] == 'POST' and not csrf_token:
                self.log_bug(
                    "Security", "High", "High",
                    f"Form {i+1}: Missing CSRF Protection",
                    "POST form lacks CSRF token protection",
                    f"Form #{i+1}"
                )
            
            self.forms.append(form_info)
    
    def test_form_validation(self):
        """Test form validation and submission"""
        for i, form in enumerate(self.forms):
            if not form['action']:
                continue
                
            action_url = urllib.parse.urljoin(self.base_url, form['action'])
            
            # Test empty submission
            try:
                if form['method'] == 'POST':
                    response = self.session.post(action_url, data={}, timeout=10)
                else:
                    response = self.session.get(action_url, params={}, timeout=10)
                
                # Check for proper error handling
                if response.status_code == 500:
                    self.log_bug(
                        "Error Handling", "High", "High",
                        f"Form {i+1}: Server Error on Empty Submission",
                        "Form submission with empty data causes server error",
                        action_url
                    )
                elif response.status_code == 200:
                    # Check if form accepted empty submission when it shouldn't
                    required_fields = [inp for inp in form['inputs'] if inp['required']]
                    if required_fields:
                        self.log_bug(
                            "Validation", "Medium", "Medium",
                            f"Form {i+1}: Accepts Empty Required Fields",
                            "Form accepts submission despite required fields being empty",
                            action_url
                        )
                
            except Exception as e:
                self.log_bug(
                    "Error Handling", "Medium", "Medium",
                    f"Form {i+1}: Submission Error",
                    f"Error during form submission: {str(e)}",
                    action_url
                )
    
    def test_xss_vulnerabilities(self, soup: BeautifulSoup):
        """Test for XSS vulnerabilities"""
        xss_payloads = [
            "<script>alert('XSS')</script>",
            "javascript:alert('XSS')",
            "<img src=x onerror=alert('XSS')>",
            "';alert('XSS');//"
        ]
        
        for i, form in enumerate(self.forms):
            if not form['action']:
                continue
                
            action_url = urllib.parse.urljoin(self.base_url, form['action'])
            
            for payload in xss_payloads:
                test_data = {}
                for input_info in form['inputs']:
                    if input_info['name'] and input_info['type'] in ['text', 'textarea']:
                        test_data[input_info['name']] = payload
                
                if not test_data:
                    continue
                
                try:
                    if form['method'] == 'POST':
                        response = self.session.post(action_url, data=test_data, timeout=10)
                    else:
                        response = self.session.get(action_url, params=test_data, timeout=10)
                    
                    # Check if payload appears unescaped in response
                    if payload in response.text and html.escape(payload) not in response.text:
                        self.log_bug(
                            "Security", "Critical", "Critical",
                            f"Form {i+1}: XSS Vulnerability",
                            f"Form is vulnerable to XSS with payload: {payload}",
                            action_url
                        )
                        break  # Found XSS, no need to test other payloads
                    
                except Exception as e:
                    continue
    
    def analyze_accessibility(self, soup: BeautifulSoup):
        """Analyze accessibility issues"""
        # Check for alt text on images
        images = soup.find_all('img')
        for img in images:
            if not img.get('alt'):
                self.log_bug(
                    "Accessibility", "Medium", "Low",
                    "Missing Alt Text",
                    "Image lacks alt attribute for screen readers",
                    img.get('src', 'Unknown image')
                )
        
        # Check for form labels
        inputs = soup.find_all('input', type=['text', 'email', 'password', 'tel', 'url'])
        for input_elem in inputs:
            input_id = input_elem.get('id')
            input_name = input_elem.get('name')
            
            # Look for associated label
            label = None
            if input_id:
                label = soup.find('label', {'for': input_id})
            
            if not label:
                # Check if input is wrapped in label
                label = input_elem.find_parent('label')
            
            if not label:
                self.log_bug(
                    "Accessibility", "Medium", "Low",
                    "Missing Form Label",
                    f"Input field '{input_name}' lacks associated label",
                    f"Input: {input_name or input_id}"
                )
        
        # Check for heading structure
        headings = soup.find_all(['h1', 'h2', 'h3', 'h4', 'h5', 'h6'])
        if not headings:
            self.log_bug(
                "Accessibility", "Low", "Low",
                "No Heading Structure",
                "Page lacks heading structure for screen readers",
                self.base_url
            )
    
    def discover_apis(self, soup: BeautifulSoup):
        """Discover potential API endpoints"""
        # Look for AJAX calls in JavaScript
        scripts = soup.find_all('script')
        api_patterns = [
            r'fetch\([\'"`]([^\'"`]+)[\'"`]',
            r'\.ajax\(\s*{[^}]*url\s*:\s*[\'"`]([^\'"`]+)[\'"`]',
            r'XMLHttpRequest.*open\([\'"`]\w+[\'"`],\s*[\'"`]([^\'"`]+)[\'"`]'
        ]
        
        for script in scripts:
            if script.string:
                for pattern in api_patterns:
                    matches = re.findall(pattern, script.string)
                    for match in matches:
                        if match not in self.apis:
                            self.apis.append(match)
        
        # Look for form actions as potential endpoints
        for form in self.forms:
            if form['action'] and form['action'] not in self.apis:
                self.apis.append(form['action'])
    
    def test_apis(self):
        """Test discovered API endpoints"""
        for api_path in self.apis:
            api_url = urllib.parse.urljoin(self.base_url, api_path)
            
            # Test different HTTP methods
            methods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']
            
            for method in methods:
                try:
                    response = self.session.request(method, api_url, timeout=10)
                    
                    # Check for verbose error messages
                    if response.status_code >= 500:
                        if any(keyword in response.text.lower() for keyword in 
                               ['stack trace', 'exception', 'error', 'debug']):
                            self.log_bug(
                                "Security", "Medium", "Medium",
                                f"API Information Disclosure",
                                f"API {api_path} exposes verbose error information",
                                api_url
                            )
                    
                    # Check for missing authentication
                    if method in ['POST', 'PUT', 'DELETE'] and response.status_code == 200:
                        self.log_bug(
                            "Security", "High", "High",
                            f"API Missing Authentication",
                            f"API {api_path} allows {method} without authentication",
                            api_url
                        )
                    
                except Exception as e:
                    continue
    
    def generate_report(self) -> str:
        """Generate comprehensive bug report"""
        if not self.bugs:
            return "No bugs found during testing."
        
        # Sort bugs by severity and priority
        severity_order = {'Critical': 0, 'High': 1, 'Medium': 2, 'Low': 3}
        priority_order = {'Critical': 0, 'High': 1, 'Medium': 2, 'Low': 3}
        
        sorted_bugs = sorted(self.bugs, key=lambda x: (
            severity_order.get(x['Severity'], 4),
            priority_order.get(x['Priority'], 4)
        ))
        
        # Create DataFrame for tabular output
        df = pd.DataFrame(sorted_bugs)
        
        # Generate summary statistics
        severity_counts = df['Severity'].value_counts()
        category_counts = df['Category'].value_counts()
        
        report = f"""
WEB APPLICATION BUG TESTING REPORT
{'=' * 50}
Target URL: {self.base_url}
Test Date: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
Total Bugs Found: {len(self.bugs)}

SEVERITY BREAKDOWN:
{'-' * 20}
"""
        for severity, count in severity_counts.items():
            report += f"{severity}: {count}\n"
        
        report += f"""
CATEGORY BREAKDOWN:
{'-' * 20}
"""
        for category, count in category_counts.items():
            report += f"{category}: {count}\n"
        
        report += f"""
DETAILED BUG REPORT:
{'-' * 20}
"""
        
        # Convert to string for better formatting
        pd.set_option('display.max_columns', None)
        pd.set_option('display.width', None)
        pd.set_option('display.max_colwidth', 80)
        
        report += df.to_string(index=False)
        
        return report
    
    def save_report(self, filename: str = None):
        """Save report to file"""
        if filename is None:
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            filename = f"bug_report_{timestamp}.txt"
        
        report = self.generate_report()
        
        with open(filename, 'w') as f:
            f.write(report)
        
        print(f"Report saved to: {filename}")
        return filename
    
    def run_comprehensive_test(self):
        """Run all tests in sequence"""
        print(f"Starting comprehensive bug testing for: {self.base_url}")
        print("=" * 60)
        
        # Test connectivity
        print("1. Testing connectivity...")
        if not self.test_connectivity():
            print("❌ Cannot connect to target URL. Stopping tests.")
            return False
        
        # Fetch and parse HTML
        print("2. Fetching and parsing HTML...")
        soup = self.fetch_and_parse_html()
        if soup is None:
            print("❌ Failed to parse HTML. Stopping tests.")
            return False
        
        # Analyze forms
        print("3. Analyzing forms...")
        self.analyze_forms(soup)
        
        # Test form validation
        print("4. Testing form validation...")
        self.test_form_validation()
        
        # Test for XSS vulnerabilities
        print("5. Testing for XSS vulnerabilities...")
        self.test_xss_vulnerabilities(soup)
        
        # Analyze accessibility
        print("6. Analyzing accessibility...")
        self.analyze_accessibility(soup)
        
        # Discover APIs
        print("7. Discovering API endpoints...")
        self.discover_apis(soup)
        
        # Test APIs
        print("8. Testing API endpoints...")
        self.test_apis()
        
        print("\n" + "=" * 60)
        print("Testing completed!")
        print(f"Total bugs found: {len(self.bugs)}")
        
        return True


def main():
    target_url = "http://27.107.74.204:8090/hob"
    
    if len(sys.argv) > 1:
        target_url = sys.argv[1]
    
    print(f"Web Application Bug Tester")
    print(f"Target: {target_url}")
    print("=" * 60)
    
    tester = WebBugTester(target_url)
    
    if tester.run_comprehensive_test():
        print("\nGenerating report...")
        report = tester.generate_report()
        print(report)
        
        # Save to file
        filename = tester.save_report()
        print(f"\nDetailed report saved to: {filename}")
    else:
        print("\nTesting failed due to connectivity issues.")
        print("Current bugs found:")
        if tester.bugs:
            report = tester.generate_report()
            print(report)


if __name__ == "__main__":
    main()