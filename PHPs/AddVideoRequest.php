<?php

//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    date_default_timezone_set('EST');
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables
    $partyTitle = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
    $songTitle = mysqli_real_escape_string($connect, $_POST["songTitle"]);
    $songID = mysqli_real_escape_string($connect, $_POST["songID"]);
    $thumbnailURL = mysqli_real_escape_string($connect, $_POST["thumbnailURL"]);
    // $partyTitle = "keys_2";
    // $songID = "nQ2x0AUkDdw";
    // $songTitle = "Tool - H. w/ Lyrics (HD)";
    $likes = 0;
    $dislikes = 0;


//Create an array of responses to send back to android via 'echo json_encode()', initially false
    $response = array();
    $response["inserted"] = false;

    function songAvailable(){
        global $connect, $songID, $partyTitle;
        $stmt = "SELECT * FROM " .$partyTitle. " WHERE link = '$songID'"; 
        $statement = mysqli_prepare($connect , $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        if(mysqli_stmt_num_rows($statement) >= 1){
            return false;
        }

    }

  //  if(songAvailable()){
        $stmt = "INSERT INTO ".$partyTitle." (title, link, likes, dislikes, thumbnailURL) VALUES (?, ?, ?, ?, ?)";
        $statement = mysqli_prepare($connect , $stmt);
        if ($statement) {
            mysqli_stmt_bind_param($statement, "ssiis", $songTitle, $songID, $likes, $dislikes, $thumbnailURL);
            mysqli_stmt_execute($statement);
            mysqli_stmt_close($statement); 
            $response["inserted"] = true;
        }
    //}


    echo json_encode($response);


    // function addVideo() {
    // global $connect, $partyTitle, $songTitle, $songID;
    //     // $statement = mysqli_prepare($connect , "INSERT INTO '$partyTitle' (title, link, likes, dislikes, played) VALUES ('$songTitle','$songID', 0, 0, '0')");
    //     $statement = mysqli_prepare($connect , "INSERT INTO youtube (title, link, likes, dislikes, played) VALUES (?, ?, ?, ?, ?)");
    //     mysqli_stmt_bind_param($statement, "ssiis", $songTitle, $songID, $likes, $dislikes, $played);
    //     mysqli_stmt_execute($statement);
    //     mysqli_stmt_close($statement);    
    // }

?>


