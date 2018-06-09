<?php
//Require password.php enables the function password_verify() to be called
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
  
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables 
    $title = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
    $songID = mysqli_real_escape_string($connect, $_POST["songID"]);


    //$password = "password";
    //$title = "there";
    //$songID = "7Ku8OVFeOOM";
    
    $response = array();
    $response["success"] = false;

    if (markedAsPlayed()){
        $response["success"] = true;
    }

    echo json_encode($response);

    function markedAsPlayed(){
        global $connect, $title, $songID;
        $stmt = "UPDATE " .$title. " SET played = '1' WHERE link = '$songID'" ;
        //$statement = mysqli_prepare($connect, "UPDATE '$title' SET played = '1' WHERE link = '$songID'");
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ($count >= 1){
            return true;
        }else{
            return false;
        }
    }

?>