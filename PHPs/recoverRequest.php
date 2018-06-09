<?php
//Establishes connection with database
    $con = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    $email = "";
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables 
    $username = mysqli_real_escape_string($con, $_POST["username"]);
   
//Test data for running file in burrow to recieve echos     
    //$username = "bennett"; 

    function usernameExists(){
        global $con, $username, $email;
        $statement = mysqli_prepare($con, "SELECT * FROM Users WHERE username = ?");
        mysqli_stmt_bind_param($statement, "s", $username);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $colUserID, $colUsername, $colPassword, $colFN, $colEmail, $colHash, $colActive, $colDOB, $colBiography);
      
        $count = mysqli_stmt_num_rows($statement);
   

        while(mysqli_stmt_fetch($statement)){
            if ($count = 0){
                return false;
            }
            else{ 
                $to = $colEmail;
                $subject = "ClutchTunes Account Recovery";
                $headers = "From: clutchtunes51@gmail.com\r\n";
                
                $response = array();  
                $response["usernameNotFound"] = false;
               // $response["email"] = $to; 

          
                
                $message_body = '
                    Hello '.$colFN.',
                    
                    It seems as though you have forgotten the password to your ClutchTunes account.

                    Please click this link to reset your password:
                    
                    cgi.soic.indiana.edu/~team51/resetPasswordPage.php?hash='.$colHash;
               
                mail( $to, $subject, $message_body, $headers );
                return true;
            }
        }
    }

//Set response as false unless proper credentials provided
    if (usernameExists()){
        $response["usernameExists"] = true;
    }
    else {
        $response["usernameExists"] = false;
    }
    echo json_encode($response);
?>