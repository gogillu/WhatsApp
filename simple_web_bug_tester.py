#!/usr/bin/env python3
"""
Simplified Web Application Bug Testing Tool
Uses only standard library + requests for maximum compatibility
Designed for testing http://27.107.74.204:8090/hob
"""

import requests
import re
import json
import urllib.parse
from datetime import datetime
import html
import sys
from typing import List, Dict, Any
from html.parser import HTMLParser


class SimpleHTMLParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.forms = []
        self.current_form = None
        self.images = []
        self.headings = []
        self.inputs = []
        self.labels = []
        self.scripts = []
        self.title = None
        self.has_html = False
        self.has_head = False
        
    def handle_starttag(self, tag, attrs):
        attrs_dict = dict(attrs)
        
        if tag == 'html':
            self.has_html = True
        elif tag == 'head':
            self.has_head = True
        elif tag == 'title':
            self.in_title = True
        elif tag == 'form':
            self.current_form = {
                'action': attrs_dict.get('action', ''),
                'method': attrs_dict.get('method', 'GET').upper(),
                'inputs': []
            }
            self.forms.append(self.current_form)
        elif tag == 'input' and self.current_form is not None:
            input_info = {
                'type': attrs_dict.get('type', 'text'),
                'name': attrs_dict.get('name', ''),
                'id': attrs_dict.get('id', ''),
                'required': 'required' in attrs_dict,
                'placeholder': attrs_dict.get('placeholder', ''),
                'autocomplete': attrs_dict.get('autocomplete', '')
            }
            self.current_form['inputs'].append(input_info)
            self.inputs.append(input_info)
        elif tag == 'textarea' and self.current_form is not None:
            input_info = {
                'type': 'textarea',
                'name': attrs_dict.get('name', ''),
                'id': attrs_dict.get('id', ''),
                'required': 'required' in attrs_dict,
                'placeholder': attrs_dict.get('placeholder', '')
            }
            self.current_form['inputs'].append(input_info)
        elif tag == 'img':
            self.images.append({
                'src': attrs_dict.get('src', ''),
                'alt': attrs_dict.get('alt', '')
            })
        elif tag in ['h1', 'h2', 'h3', 'h4', 'h5', 'h6']:
            self.headings.append(tag)
        elif tag == 'label':
            self.labels.append({
                'for': attrs_dict.get('for', '')
            })
        elif tag == 'script':
            self.in_script = True
            self.current_script = ''
    
    def handle_data(self, data):
        if hasattr(self, 'in_title') and self.in_title:
            self.title = data
        elif hasattr(self, 'in_script') and self.in_script:
            self.current_script += data
    
    def handle_endtag(self, tag):
        if tag == 'title':
            self.in_title = False
        elif tag == 'script':
            self.in_script = False
            if hasattr(self, 'current_script'):
                self.scripts.append(self.current_script)
                self.current_script = ''


class WebBugTester:
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip('/')
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'WebBugTester/1.0 (Security Testing Tool)'
        })
        self.bugs = []
        self.parser = None
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
    
    def fetch_and_parse_html(self) -> SimpleHTMLParser:
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
                return None
            
            parser = SimpleHTMLParser()
            parser.feed(response.text)
            
            # Check for basic HTML structure
            if not parser.has_html:
                self.log_bug(
                    "HTML", "Medium", "Medium",
                    "Missing HTML Tag",
                    "Document lacks proper HTML tag",
                    self.base_url
                )
            
            if not parser.has_head:
                self.log_bug(
                    "HTML", "Low", "Low",
                    "Missing HEAD Section",
                    "Document lacks HEAD section",
                    self.base_url
                )
            
            if not parser.title:
                self.log_bug(
                    "SEO", "Low", "Low",
                    "Missing Title Tag",
                    "Document lacks title tag for SEO",
                    self.base_url
                )
            
            self.parser = parser
            return parser
            
        except Exception as e:
            self.log_bug(
                "HTML", "High", "High",
                "HTML Parsing Error",
                f"Failed to parse HTML: {str(e)}",
                self.base_url
            )
            return None
    
    def analyze_forms(self):
        """Analyze forms for potential issues"""
        if not self.parser or not self.parser.forms:
            self.log_bug(
                "UI/UX", "Low", "Low",
                "No Forms Found",
                "No forms detected on the page",
                self.base_url
            )
            return
        
        for i, form in enumerate(self.parser.forms):
            # Check form action
            if not form['action']:
                self.log_bug(
                    "Security", "Medium", "Medium",
                    f"Form {i+1}: Missing Action Attribute",
                    "Form lacks action attribute, may cause unexpected behavior",
                    f"Form #{i+1}"
                )
            
            # Check form method
            if form['method'] not in ['GET', 'POST']:
                self.log_bug(
                    "HTML", "Medium", "Medium",
                    f"Form {i+1}: Invalid Method",
                    f"Form method '{form['method']}' is not standard",
                    f"Form #{i+1}"
                )
            
            # Analyze form inputs
            for input_elem in form['inputs']:
                # Check for missing name attribute
                if not input_elem['name'] and input_elem['type'] not in ['button', 'submit']:
                    self.log_bug(
                        "HTML", "Medium", "Medium",
                        f"Form {i+1}: Input Missing Name",
                        "Input element lacks name attribute for form submission",
                        f"Form #{i+1}"
                    )
                
                # Check for password fields without proper security
                if input_elem['type'] == 'password':
                    if form['method'] == 'GET':
                        self.log_bug(
                            "Security", "High", "High",
                            f"Form {i+1}: Password via GET",
                            "Password field in form using GET method (security risk)",
                            f"Form #{i+1}"
                        )
                    
                    # Check for autocomplete on password fields
                    if input_elem['autocomplete'] != 'off':
                        self.log_bug(
                            "Security", "Medium", "Medium",
                            f"Form {i+1}: Password Autocomplete",
                            "Password field allows autocomplete (potential security risk)",
                            f"Form #{i+1}"
                        )
            
            # Check for CSRF protection
            has_csrf = any(inp['name'] and re.search(r'csrf|token', inp['name'], re.I) 
                          for inp in form['inputs'])
            if form['method'] == 'POST' and not has_csrf:
                self.log_bug(
                    "Security", "High", "High",
                    f"Form {i+1}: Missing CSRF Protection",
                    "POST form lacks CSRF token protection",
                    f"Form #{i+1}"
                )
    
    def test_form_validation(self):
        """Test form validation and submission"""
        if not self.parser:
            return
            
        for i, form in enumerate(self.parser.forms):
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
    
    def test_xss_vulnerabilities(self):
        """Test for XSS vulnerabilities"""
        if not self.parser:
            return
            
        xss_payloads = [
            "<script>alert('XSS')</script>",
            "javascript:alert('XSS')",
            "<img src=x onerror=alert('XSS')>",
            "';alert('XSS');//"
        ]
        
        for i, form in enumerate(self.parser.forms):
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
    
    def analyze_accessibility(self):
        """Analyze accessibility issues"""
        if not self.parser:
            return
            
        # Check for alt text on images
        for img in self.parser.images:
            if not img['alt']:
                self.log_bug(
                    "Accessibility", "Medium", "Low",
                    "Missing Alt Text",
                    "Image lacks alt attribute for screen readers",
                    img['src'] or 'Unknown image'
                )
        
        # Check for form labels
        for input_elem in self.parser.inputs:
            if input_elem['type'] in ['text', 'email', 'password', 'tel', 'url']:
                input_id = input_elem['id']
                
                # Look for associated label
                has_label = any(label['for'] == input_id for label in self.parser.labels if input_id)
                
                if not has_label:
                    self.log_bug(
                        "Accessibility", "Medium", "Low",
                        "Missing Form Label",
                        f"Input field '{input_elem['name']}' lacks associated label",
                        f"Input: {input_elem['name'] or input_elem['id']}"
                    )
        
        # Check for heading structure
        if not self.parser.headings:
            self.log_bug(
                "Accessibility", "Low", "Low",
                "No Heading Structure",
                "Page lacks heading structure for screen readers",
                self.base_url
            )
    
    def discover_apis(self):
        """Discover potential API endpoints"""
        if not self.parser:
            return
            
        # Look for AJAX calls in JavaScript
        api_patterns = [
            r'fetch\([\'"`]([^\'"`]+)[\'"`]',
            r'\.ajax\(\s*{[^}]*url\s*:\s*[\'"`]([^\'"`]+)[\'"`]',
            r'XMLHttpRequest.*open\([\'"`]\w+[\'"`],\s*[\'"`]([^\'"`]+)[\'"`]'
        ]
        
        for script_content in self.parser.scripts:
            for pattern in api_patterns:
                matches = re.findall(pattern, script_content)
                for match in matches:
                    if match not in self.apis:
                        self.apis.append(match)
        
        # Look for form actions as potential endpoints
        for form in self.parser.forms:
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
        
        # Generate summary statistics
        severity_counts = {}
        category_counts = {}
        
        for bug in self.bugs:
            severity = bug['Severity']
            category = bug['Category']
            severity_counts[severity] = severity_counts.get(severity, 0) + 1
            category_counts[category] = category_counts.get(category, 0) + 1
        
        report = f"""
WEB APPLICATION BUG TESTING REPORT
{'=' * 50}
Target URL: {self.base_url}
Test Date: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
Total Bugs Found: {len(self.bugs)}

SEVERITY BREAKDOWN:
{'-' * 20}
"""
        for severity in ['Critical', 'High', 'Medium', 'Low']:
            if severity in severity_counts:
                report += f"{severity}: {severity_counts[severity]}\n"
        
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
        
        # Create tabular format manually
        headers = ['#', 'Category', 'Severity', 'Priority', 'Title', 'Description', 'Location']
        col_widths = [3, 15, 10, 10, 40, 60, 30]
        
        # Header row
        header_row = "| "
        for i, header in enumerate(headers):
            header_row += f"{header:<{col_widths[i]}} | "
        report += header_row + "\n"
        
        # Separator row
        sep_row = "|"
        for width in col_widths:
            sep_row += "-" * (width + 2) + "|"
        report += sep_row + "\n"
        
        # Data rows
        for i, bug in enumerate(sorted_bugs, 1):
            row = f"| {i:<{col_widths[0]}} | "
            row += f"{bug['Category']:<{col_widths[1]}} | "
            row += f"{bug['Severity']:<{col_widths[2]}} | "
            row += f"{bug['Priority']:<{col_widths[3]}} | "
            
            # Truncate long text to fit columns
            title = bug['Title'][:col_widths[4]-3] + "..." if len(bug['Title']) > col_widths[4] else bug['Title']
            desc = bug['Description'][:col_widths[5]-3] + "..." if len(bug['Description']) > col_widths[5] else bug['Description']
            location = bug['Location'][:col_widths[6]-3] + "..." if len(bug['Location']) > col_widths[6] else bug['Location']
            
            row += f"{title:<{col_widths[4]}} | "
            row += f"{desc:<{col_widths[5]}} | "
            row += f"{location:<{col_widths[6]}} |"
            
            report += row + "\n"
        
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
            print("❌ Cannot connect to target URL. Testing stopped.")
            return False
        
        # Fetch and parse HTML
        print("2. Fetching and parsing HTML...")
        parser = self.fetch_and_parse_html()
        if parser is None:
            print("❌ Failed to parse HTML. Testing stopped.")
            return False
        
        # Analyze forms
        print("3. Analyzing forms...")
        self.analyze_forms()
        
        # Test form validation
        print("4. Testing form validation...")
        self.test_form_validation()
        
        # Test for XSS vulnerabilities
        print("5. Testing for XSS vulnerabilities...")
        self.test_xss_vulnerabilities()
        
        # Analyze accessibility
        print("6. Analyzing accessibility...")
        self.analyze_accessibility()
        
        # Discover APIs
        print("7. Discovering API endpoints...")
        self.discover_apis()
        
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
    
    print(f"Web Application Bug Tester (Simplified)")
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