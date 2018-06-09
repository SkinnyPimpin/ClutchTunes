<?php
//Require password.php enables the function password_hash() to be called
    require("password.php");
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    
    
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables
    $username = mysqli_real_escape_string($connect, $_POST["username"]);
    $password1 = mysqli_real_escape_string($connect, $_POST["password"]);
    $first_name = mysqli_real_escape_string($connect,$_POST["firstName"]);
    $email = mysqli_real_escape_string($connect,$_POST["email"]);
     $hash = mysqli_real_escape_string($connect, md5( rand(0,1000)));
    //Get Birthday
    $DOB = mysqli_real_escape_string($connect, $_POST["dob"]);

   // $username = "bennettTest";
   // $password1 = "pasdkjsd";
   // $first_name = "bennettTest";
   // $email = "belks@dflks.com";
   // $DOB = "06-30-1994";


//Registers User in the Users table, called if email and username both available
    function registerUser() {
        global $connect, $username, $password1, $first_name, $email, $hash, $DOB;
        $passwordHash = password_hash($password1, PASSWORD_DEFAULT);
        $statement = mysqli_prepare($connect, "INSERT INTO Users (username, password, firstName, email, hash, dob) VALUES (?, ?, ?, ?, ?, ?)");
        mysqli_stmt_bind_param($statement, "ssssss", $username, $passwordHash, $first_name, $email, $hash, $DOB);
        mysqli_stmt_execute($statement);
        mysqli_stmt_close($statement);
        
        $sql = "CREATE TABLE " . $username . " (id int(4) AUTO_INCREMENT NOT NULL, title varchar(225) not null, link varchar(255) NOT NULL, thumbnailURL varchar(250) DEFAULT 'ClutchTunes', primary key(id))";

        mysqli_query($connect, $sql);      
    }
//Checks Users table for existing username, if available returns true
    function usernameAvailable() {
        global $connect, $username;
        $statement = mysqli_prepare($connect, "SELECT * FROM Users WHERE username = ?"); 
        mysqli_stmt_bind_param($statement, "s", $username);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        if ($count < 1){
            return true; 
        }else {
            return false; 
        }
    }
//Checks Users table for existing email, if available returns true
    function emailAvailable() {
        global $connect, $email;
        $statement = mysqli_prepare($connect, "SELECT * FROM Users WHERE email = ?"); 
        mysqli_stmt_bind_param($statement, "s", $email);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        if ($count < 1){
            return true; 
        }else {
            return false; 
        }
    }  
  
//Create an array of responses to send back to android via 'echo json_encode()', initially false
    $response = array();
    $response["username"] = false;
    $response["email"] = false;
  
  $to = $email;
  $subject = 'ClutchTunes Activation Link';
  $headers = "From: clutchtunes51@gmail.com\r\n";
  $message_body = '
        Hello '.$first_name.',
        Thank you for signing up with ClutchTunes!
        Please click this link to activate your account:
    
    cgi.soic.indiana.edu/~team51/verify.php?hash='.$hash;
    if (usernameAvailable()){
      $response["username"] = true;
    }
    if (emailAvailable()){
      $response["email"] = true;
    }
    if (usernameAvailable() && emailAvailable()){
  registerUser();      
  mail( $to, $subject, $message_body, $headers );
    }
//Echos response
    echo json_encode($response);
?>