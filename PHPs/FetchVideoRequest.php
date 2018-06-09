<?php

//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    date_default_timezone_set('EST');
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables
    $partyTitle = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
    //$partyTitle = "played";


//Create an array of responses to send back to android via 'echo json_encode()', initially false
    $response = array();
    $response["weHaveASong"] = false;
    $response["songID"] = "";
    // $response["songTitle"] = "";
    

    $stmt = "SELECT link FROM " . $partyTitle . " WHERE played = '0' ORDER BY cornhole DESC";
    $statement = mysqli_prepare($connect , $stmt);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colLink);

    while(mysqli_stmt_fetch($statement)){
        $response["songID"] = $response["songID"]. " ". $colLink;
        $response["weHaveASong"] = true;
    }

    // $stmt = "SELECT title FROM " . $partyTitle . " WHERE played = '0'";
    // $statement = mysqli_prepare($connect , $stmt);
    // mysqli_stmt_execute($statement);
    // mysqli_stmt_store_result($statement);
    // mysqli_stmt_bind_result($statement, $colTitle);

    // while(mysqli_stmt_fetch($statement)){
    //     $response["SongTitle"] = $response["SongTitle"]. " ". $colTitle;
    // }


    echo json_encode($response);


?>


