<?php
session_start(); // Starting Session
$error=''; // Variable To Store Error Message
if (isset($_POST['submit1'])) {
if (empty($_POST['username']) || empty($_POST['password'])) {
$error = "Username or Password is invalid";
}
else
{
// Define $username and $password
	include('db.php'); 
$username=$_POST['username'];
$password=$_POST['password'];
// Establishing Connection with Server by passing server_name, user_id and password as a parameter

// To protect MySQL injection for Security purpose
$username = stripslashes($username);
$password = stripslashes($password);
$username = mysqli_real_escape_string($con,$username);
$password = mysqli_real_escape_string($con,$password);
// Selecting Database

// SQL query to fetch information of registerd users and finds user match.
$query = mysqli_query($con,"select * from login where password='$password' AND username='$username'");
$rows = mysqli_num_rows($query) or die ((mysqli_error($con)));
if ($rows == 1) {
$_SESSION['login_user']=$username; // Initializing Session
header("location: OpenPR.php"); // Redirecting To Other Page
} else {
$error = "Username or Password is invalid";
}
mysqli_close($connection); // Closing Connection
}
if(!isset($_SESSION['login_user'])){
    header('location:index.php');
}

if (count(get_included_files()) == 1) header('index.php');

// on all screens requiring login, redirect if NOT logged in

}
?>