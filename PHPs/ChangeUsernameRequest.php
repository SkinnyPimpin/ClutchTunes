<?php 
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");

    $username = mysqli_real_escape_string($connect, $_POST["username"]);
    $newUsername = mysqli_real_escape_string($connect, $_POST["newUsername"]);

    //$username = "bennett";
    //$newUsername = "bdboy";
    //$title = "lo";
    $response = array();
    $response["success"]  = false;
    $response["usernameAvailable"] = false;

    if (usernameAvailable()){
        $response["usernameAvailable"] = true;
        // Select party with matching title  with active column = "1"
        $statement = mysqli_prepare($connect, "UPDATE Users SET username='$newUsername' WHERE username='$username'");
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ( $count == 1){ 
            $response["success"] = true;
        }
        else{
            $response["success"] = false;
        }
    }

    //echo $response["success"];

    function usernameAvailable() {
        global $connect, $newUsername;
        $statement = mysqli_prepare($connect, "SELECT * FROM Users WHERE username = ?"); 
        mysqli_stmt_bind_param($statement, "s", $newUsername);
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
    echo json_encode($response);
    //echo $response["success"];
?>