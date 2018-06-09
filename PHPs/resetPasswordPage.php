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
	$hash = $_GET['hash'];
    //$email = mysqli_real_escape_string($connect,$_POST["email"]);
    //$hash = mysqli_real_escape_string($connect, md5( rand(0,1000)));
    //$username = $_GET["username"];
	//$username = 'bennett';

	//$email = "bedierckman@gmail.com";
	//echo "connection:   " .$connect;
	
	
    // Select user with matching email and hash, who hasn't verified their account yet (active = 0)
    $statement = mysqli_prepare($connect, "SELECT * FROM Users WHERE hash = ?"); 
    mysqli_stmt_bind_param($statement, "s", $hash);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    $count = mysqli_stmt_num_rows($statement);
    mysqli_stmt_close($statement); 

    if ( $count == 0)
    { 
        $_SESSION['message'] = "Something went wrong...";
        echo "<script type='text/javascript'> document.location = 'error.php'; </script>";
		echo "we are here";
    }
}
else {
    $_SESSION['message'] = "Invalid parameters provided for account verification!";
    echo "<script type='text/javascript'> document.location = 'error.php'; </script>";
	echo "we arent there";
} 
?>
<!DOCTYPE html>
<html >
<head>
  <meta charset="UTF-8">
  <title>Reset Your Password</title>
  <link href='http://fonts.googleapis.com/css?family=Titillium+Web:400,300,600' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/5.0.0/normalize.min.css">
  <link rel="stylesheet" href="css/style.css">
</head>

<body>
    <div class="form">

          <h1>Choose Your New Password</h1>
          
          <form action="resetPassword.php" method="post">
              
          <div class="field-wrap">
            <label>
              New Password<span class="req">*</span>
            </label>
            <input type="password"required name="newpassword" autocomplete="off"/>
          </div>
              
          <div class="field-wrap">
            <label>
              Confirm New Password<span class="req">*</span>
            </label>
            <input type="password"required name="confirmpassword" autocomplete="off"/>
          </div>
          
          <!-- This input field is needed, to get the email of the user -->
    
          <input type="hidden" name="hash" value="<?= $hash ?>">   
          <button class="button button-block"/>Apply</button>
          
          </form>

    </div>
<script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
<script src="js/index.js"></script>

</body>
</html>