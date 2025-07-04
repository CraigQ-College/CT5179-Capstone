// auth.js - Handles user authentication (sign up, login, forgot password, reset password) using Supabase

// 1. Connect to your Supabase project
const SUPABASE_URL = 'https://qeztinzrtdvcbbvlywdt.supabase.co'; // Your Supabase project URL
const SUPABASE_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFlenRpbnpydGR2Y2Jidmx5d2R0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc0MDE1NDcsImV4cCI6MjA2Mjk3NzU0N30.I_P0VI6h7fg3aPirciLp8WGOGcJMyBecNhEPDOAIxRk'; // Your Supabase public anon key
const supabaseClient = supabase.createClient(SUPABASE_URL, SUPABASE_KEY);

// 2. Remember the user's email in the browser so it's easier to log in next time
function rememberEmail(email) {
  localStorage.setItem('rememberedEmail', email);
}

// 3. If we have remembered an email, fill it in automatically on the form
function prefillEmail(inputId) {
  const savedEmail = localStorage.getItem('rememberedEmail');
  if (savedEmail) {
    const input = document.getElementById(inputId);
    if (input) input.value = savedEmail;
  }
}

// 4. Handle user login
// This runs when the user submits the login form
async function handleLogin(event) {
  event.preventDefault(); // Stop the page from reloading
  const email = document.getElementById('userEmail').value;
  const password = document.getElementById('userPassword').value;
  // Ask Supabase to log the user in
  const { data, error } = await supabaseClient.auth.signInWithPassword({ email, password });
  if (error) {
    // If there was a problem, show the error message
    showError('login-error', error.message);
  } else if (data && data.session) {
    // If successful, remember the email and send token to backend
    rememberEmail(email);
    console.log('Sending token to backend:', data.session.access_token);
    await fetch('/api/session', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token: data.session.access_token })
    });
    window.location.href = '/dashboard';
  }
}

// 5. Handle user sign up (register)
// This runs when the user submits the sign up form
async function handleSignUp(event) {
  event.preventDefault();
  const username = document.getElementById('username').value.trim();
  const email = document.getElementById('userEmail').value.trim();
  const password = document.getElementById('userPassword').value;
  const confirm = document.getElementById('confirmPassword').value;

  // Username validation
  if (!username || username.length < 3) {
    showError('signup-error', 'Username must be at least 3 characters.');
    return;
  }

  // Email validation
  const emailPattern = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
  if (!emailPattern.test(email)) {
    showError('signup-error', 'Please enter a valid email address.');
    return;
  }

  // Password validation
  if (password.length < 6) {
    showError('signup-error', 'Password must be at least 6 characters.');
    return;
  }
  if (password !== confirm) {
    showError('signup-error', 'Passwords do not match.');
    return;
  }

  // Ask Supabase to create a new user with display_name as metadata
  const { error } = await supabaseClient.auth.signUp({
    email,
    password,
    options: {
      data: { display_name: username }
    }
  });
  if (error) {
    showError('signup-error', error.message);
  } else {
    rememberEmail(email);
    window.location.href = '/dashboard';
  }
}

// 6. Handle forgot password
// This runs when the user submits the forgot password form
async function handleForgotPassword(event) {
  event.preventDefault();
  const email = document.getElementById('email').value;
  // Ask Supabase to send a password reset email
  const { error } = await supabaseClient.auth.resetPasswordForEmail(email);
  if (error) {
    showError('forgot-error', error.message);
  } else {
    showSuccess('forgot-success', 'Password reset email sent! Please check your inbox.');
  }
}

// 7. Handle reset password
// This runs when the user submits the reset password form (after clicking the link in their email)
async function handleResetPassword(event) {
  event.preventDefault();
  const password = document.getElementById('newPassword').value;
  const confirm = document.getElementById('confirmPassword').value;
  // Check if the passwords match
  if (password !== confirm) {
    showError('reset-error', 'Passwords do not match.');
    return;
  }
  // Ask Supabase to update the user's password
  const { error } = await supabaseClient.auth.updateUser({ password });
  if (error) {
    showError('reset-error', error.message);
  } else {
    showSuccess('reset-success', 'Password has been reset! You can now log in.');
    setTimeout(() => window.location.href = '/log-in', 2000); // Redirect to login after 2 seconds
  }
}

// 8. Show an error message in the UI
function showError(id, message) {
  const el = document.getElementById(id);
  if (el) {
    el.textContent = message;
    el.style.display = 'block';
  }
}

// 9. Show a success message in the UI
function showSuccess(id, message) {
  const el = document.getElementById(id);
  if (el) {
    el.textContent = message;
    el.style.display = 'block';
  }
}

// 10. Make these functions available to the HTML pages
window.authHandlers = {
  handleLogin,
  handleSignUp,
  handleForgotPassword,
  handleResetPassword,
  prefillEmail
}; 