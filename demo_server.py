#!/usr/bin/env python3
"""
Demo HTML Server for Bug Testing
Creates a local server with various bugs for testing the bug detection tool
"""

import http.server
import socketserver
import threading
import time


demo_html = """<!DOCTYPE html>
<html>
<head>
    <title>Demo Bug Testing Page</title>
</head>
<body>
    <h1>Demo Web Application with Bugs</h1>
    
    <!-- Form with multiple issues -->
    <form method="GET" action="/submit">
        <h2>Login Form (Insecure)</h2>
        <input type="text" name="username" placeholder="Username">
        <input type="password" name="password" placeholder="Password">
        <input type="submit" value="Login">
    </form>
    
    <!-- Form without CSRF protection -->
    <form method="POST" action="/transfer">
        <h2>Money Transfer Form</h2>
        <input type="text" name="amount" placeholder="Amount" required>
        <input type="text" name="account" placeholder="Account Number">
        <input type="submit" value="Transfer Money">
    </form>
    
    <!-- Form with XSS vulnerability -->
    <form method="GET" action="/search">
        <h2>Search Form</h2>
        <input type="text" name="query" placeholder="Search query">
        <input type="submit" value="Search">
    </form>
    
    <!-- Images without alt text -->
    <img src="logo.png" width="100" height="50">
    <img src="banner.jpg" alt="">
    
    <!-- Form inputs without labels -->
    <form method="POST" action="/contact">
        <h2>Contact Form</h2>
        <input type="text" name="name" placeholder="Your Name">
        <input type="email" name="email" placeholder="Your Email">
        <textarea name="message" placeholder="Message"></textarea>
        <input type="submit" value="Send Message">
    </form>
    
    <!-- JavaScript with API calls -->
    <script>
        function loadData() {
            fetch('/api/data')
                .then(response => response.json())
                .then(data => console.log(data));
        }
        
        function saveUser() {
            fetch('/api/users', {
                method: 'POST',
                body: JSON.stringify({name: 'test'})
            });
        }
    </script>
</body>
</html>"""


class DemoHandler(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/':
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(demo_html.encode())
        elif self.path.startswith('/search'):
            # Simulate XSS vulnerability
            query = self.path.split('query=')[-1] if 'query=' in self.path else ''
            response = f"<html><body><h1>Search Results</h1><p>You searched for: {query}</p></body></html>"
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(response.encode())
        else:
            self.send_response(404)
            self.end_headers()
    
    def do_POST(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write(b"<html><body><h1>Form Submitted</h1></body></html>")
    
    def log_message(self, format, *args):
        pass  # Suppress logging


def start_demo_server():
    PORT = 8088
    with socketserver.TCPServer(("", PORT), DemoHandler) as httpd:
        print(f"Demo server running at http://localhost:{PORT}")
        httpd.serve_forever()


if __name__ == "__main__":
    # Start server in background
    server_thread = threading.Thread(target=start_demo_server, daemon=True)
    server_thread.start()
    
    print("Starting demo server...")
    time.sleep(2)
    
    # Import and run the bug tester
    import subprocess
    result = subprocess.run([
        'python3', 'simple_web_bug_tester.py', 'http://localhost:8088'
    ], capture_output=True, text=True)
    
    print(result.stdout)
    if result.stderr:
        print("Errors:", result.stderr)