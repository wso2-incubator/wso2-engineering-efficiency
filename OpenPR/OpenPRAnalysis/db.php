<?php

$localhost="";
$username="";
$password="";
$DBName="";
$con = mysqli_connect($localhost,$username,$password,$DBName);
if(!$con)
{
	echo "Database Not Connected";
}

?>
