<?php 
/* Verifies registered user email, the link to this page
   is included in the register3.php email message 
*/
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    
session_start();
// Make sure email and hash variables aren't empty
if(isset($_GET['hash']) && !empty($_GET['hash']))
{
    //$email = mysqli_real_escape_string($connect,$_POST["email"]);
    //$hash = mysqli_real_escape_string($connect, md5( rand(0,1000)));
    $email = $_GET["email"];
	$hash = $_GET["hash"];
	
	//echo "connection:   " .$connect;
	echo "this is the hash: " . $hash;
	
    // Select user with matching email and hash, who hasn't verified their account yet (active = 0)
	$statement = mysqli_prepare($connect, "SELECT * FROM Users WHERE hash='$hash' AND active='0'");
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
	$count = mysqli_stmt_num_rows($statement);
	echo "   This is the count: ".$count;
    if ( $count == 0)
    { 
        $_SESSION['message'] = "Account has already been activated or the URL is invalid!";
        echo "<script type='text/javascript'> document.location = 'error.php'; </script>";
		echo "we are here";
    }
    else {
        $_SESSION['message'] = "Your account has been activated! You May now Successfully Login to your ClutchTunes Account!!!";
        
        // Set the user status to active (active = 1)
        //$mysqli->query("UPDATE users SET active='1' WHERE email='$email'") or die($mysqli->error);
		$statement = mysqli_prepare($connect, "UPDATE Users SET active='1' WHERE hash='$hash'");
    mysqli_stmt_execute($statement);
        $_SESSION['active'] = 1;
        
        echo "<script type='text/javascript'> document.location = 'success.php'; </script>";
    }
}
else {
    $_SESSION['message'] = "Invalid parameters provided for account verification!";
    echo "<script type='text/javascript'> document.location = 'error.php'; </script>";
	echo "we arent there";
} //had an ending tag