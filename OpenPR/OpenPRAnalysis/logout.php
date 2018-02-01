<?php
session_start();
session_destroy(); // Destroying All Sessions
header("Location: https://identity.cloud.wso2.com/user-portal/logout"); // Redirecting To Home Page

?>