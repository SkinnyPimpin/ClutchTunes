<?php
require ("password.php");
//Establishes connection with database
$connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51") or die("Could not connect: " . mysqli_connect_error());

session_start();

// Make sure the form is being submitted with method="post"
if ($_SERVER['REQUEST_METHOD'] == 'POST') { 

    // Make sure the two passwords match
    if ( $_POST['newpassword'] == $_POST['confirmpassword'] ) { 

        $new_password = password_hash($_POST['newpassword'], PASSWORD_DEFAULT);
        $hash = $_POST['hash'];

        $sql = "UPDATE Users SET password='$new_password' WHERE hash='$hash'";
        mysqli_query($connect, $sql);
        //$count = mysqli_affected_rows($connect);
        //echo "   This is the count: ".$count;

        if ( mysqli_affected_rows($connect) == 1 ) {
        mysqli_close($connect);
        $_SESSION['message'] = "Your password has been reset successfully!";
        echo "<script type='text/javascript'> document.location = 'success.php'; </script>";
        }

        else {
        $_SESSION['message'] = "Something went wrong!";
        echo "<script type='text/javascript'> document.location = 'error.php'; </script>";
        }
    }
    else {
        $_SESSION['message'] = "Two passwords you entered don't match, try again!";
        echo "<script type='text/javascript'> document.location = 'error.php'; </script>";
    }

}//this did have an ending tag at the end
