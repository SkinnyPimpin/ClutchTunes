<?php
//Require password.php enables the function password_verify() to be called
    require("password.php");
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
  
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables 
    $password = mysqli_real_escape_string($connect, $_POST["partyPassword"]);
    $title = mysqli_real_escape_string($connect, $_POST["partyTitle"]);

    //$password = "password";
    //$title = "bennettsCurrentParty";
 
    function checkPasswords(){
        global $connect, $title, $password;
        $statement = mysqli_prepare($connect, "SELECT * FROM Parties WHERE title = ?");
        mysqli_stmt_bind_param($statement, "s", $title);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $colID, $colTitle, $colPassword, $colDemographic, $colRadius, $colTimeframe, $colActive, $colAttending, $colLatitude, $colLongitude);

        while(mysqli_stmt_fetch($statement)){
    	//echo $colPassword;
            if (password_verify($password, $colPassword)) {
    	    	      return true; 
            }else{
                return false;
            } 
        }
    } 

    function addOneToAttending(){
        global $connect, $title;
        $statement = mysqli_prepare($connect, "UPDATE Parties SET attending = attending+1 WHERE title='$title' AND active='1'");
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ($count == 1){
            return true;
        }else{
            return false;
        }
    }

    $response = array();
    $response["success"] = false;

    if (checkPasswords()){
        if(addOneToAttending()){
            $response["success"] = true;
        }else{
            $response["success"] = false;
        }
    }else{
        $response["success"] = false;
    }
    echo json_encode($response);
?>