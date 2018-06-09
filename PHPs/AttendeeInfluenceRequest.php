<?php
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    date_default_timezone_set('EST');
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables
    $partyTitle = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
    $videoTitle = mysqli_real_escape_string($connect, $_POST["videoTitle"]);
    $userInfluence = mysqli_real_escape_string($connect, $_POST["userInfluence"]);

    $videoID = "";

    // $partyTitle = "thumbnail4";
    // $videoTitle = "Slow Motion - Juvenile";
    // $userInfluence = "dislike";
    // $userInfluence = "swapL4D"; // can be like, dislike, swapL4D, or swapD4L 

    $stmt = "SELECT link FROM " .$partyTitle. " WHERE title = ?";
    //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
    $statement = mysqli_prepare($connect, $stmt);
    mysqli_stmt_bind_param($statement, "s", $videoTitle);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colVideoID);

    while(mysqli_stmt_fetch($statement)){
        $videoID = $colVideoID;
    }   

//Create an array of responses to send back to android via 'echo json_encode()', initially false
    $response = array();
    $response["success"] = false;
    $response["likes"] = 0;
    $response["dislikes"] = 0;
    $response["notClutchVideoDeleted"] = false;

    if ($userInfluence == "like"){
        $stmt = "UPDATE " .$partyTitle. " SET likes = likes+1 WHERE link = '$videoID'" ;
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ($count >= 1){
            $stmt = "UPDATE " .$partyTitle. " SET cornhole = likes-dislikes WHERE link = '$videoID'" ;
            $statement = mysqli_prepare($connect, $stmt);
            mysqli_stmt_execute($statement);
            mysqli_stmt_store_result($statement);
            $count = mysqli_affected_rows($connect);
            mysqli_stmt_close($statement); 
                if ($count >= 1){
                    $response["success"] = true;
                }
        }
    }

    else if ($userInfluence == "dislike"){
        $stmt = "UPDATE " .$partyTitle. " SET dislikes = dislikes+1 WHERE link = '$videoID'" ;
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ($count >= 1){
            $stmt = "UPDATE " .$partyTitle. " SET cornhole = likes-dislikes WHERE link = '$videoID'" ;
            $statement = mysqli_prepare($connect, $stmt);
            mysqli_stmt_execute($statement);
            mysqli_stmt_store_result($statement);
            $count = mysqli_affected_rows($connect);
            mysqli_stmt_close($statement); 
            if ($count >= 1){  //NOW CHECK TO SEE IF WE NEED TO TOSS OUT SONG
                if (videoIsNotClutch()){
                    if (dropLameVideo()){
                        $response["notClutchVideoDeleted"] = true;
                    }
                }
                $response["success"] = true;
            }
        }
    }
    else if ($userInfluence == "swapL4D"){
        $stmt = "UPDATE " .$partyTitle. " SET likes = likes-1, dislikes = dislikes+1 WHERE link = '$videoID'" ;
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ($count >= 1){
           $stmt = "UPDATE " .$partyTitle. " SET cornhole = likes-dislikes WHERE link = '$videoID'" ;
            $statement = mysqli_prepare($connect, $stmt);
            mysqli_stmt_execute($statement);
            mysqli_stmt_store_result($statement);
            $count = mysqli_affected_rows($connect);
            mysqli_stmt_close($statement); 
            if ($count >= 1){  //NOW CHECK TO SEE IF WE NEED TO TOSS OUT SONG
                if (videoIsNotClutch()){
                    if (dropLameVideo()){
                        $response["notClutchVideoDeleted"] = true;
                    }
                }
                $response["success"] = true;
            }
        }
    }
    else if ($userInfluence == "swapD4L"){
        $stmt = "UPDATE " .$partyTitle. " SET likes = likes+1, dislikes = dislikes-1 WHERE link = '$videoID'" ;
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ($count >= 1){
            $stmt = "UPDATE " .$partyTitle. " SET cornhole = likes-dislikes WHERE link = '$videoID'" ;
            $statement = mysqli_prepare($connect, $stmt);
            mysqli_stmt_execute($statement);
            mysqli_stmt_store_result($statement);
            $count = mysqli_affected_rows($connect);
            mysqli_stmt_close($statement); 
                if ($count >= 1){
                    $response["success"] = true;
                }
        }
    }

    $stmt = "SELECT likes FROM " .$partyTitle. " WHERE title = ?";
    //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
    $statement = mysqli_prepare($connect, $stmt);
    mysqli_stmt_bind_param($statement, "s", $videoTitle);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colLikes);
    while(mysqli_stmt_fetch($statement)){
        $response["likes"] += $colLikes;
    }   

    $stmt = "SELECT dislikes FROM " .$partyTitle. " WHERE title = ?";
    //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
    $statement = mysqli_prepare($connect, $stmt);
    mysqli_stmt_bind_param($statement, "s", $videoTitle);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colDislikes);
    while(mysqli_stmt_fetch($statement)){
        $response["dislikes"] += $colDislikes;
    }   

    echo json_encode($response);

    function dropLameVideo(){
        global $connect, $partyTitle, $videoTitle; 
        $stmt = "DELETE FROM " . $partyTitle . " WHERE title = '" . $videoTitle . "'"; 
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_affected_rows($connect);
        mysqli_stmt_close($statement); 
        if ( $count >= 1){ 
            return true;
        }
        else{
            return false;
        }
    }

    function videoIsNotClutch(){
        //when this function is called the dislike has already been added to the database
        global $connect, $partyTitle, $videoTitle; 

        $dislikes = 0;  
        $attending = 0;

        $stmt = "SELECT dislikes FROM " .$partyTitle. " WHERE title = ?";
        //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_bind_param($statement, "s", $videoTitle);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $colDislikes);
        while(mysqli_stmt_fetch($statement)){
            $dislikes += $colDislikes;
        }  

        $stmt = "SELECT attending FROM Parties WHERE title = ?";
        //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_bind_param($statement, "s", $partyTitle);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $colAttending);
        while(mysqli_stmt_fetch($statement)){
            $attending += $colAttending;
        }  

        if ($attending <= 10){
            if( $dislikes / $attending > 0.6 ){
                return true;
            }
            else{
                return false;
            }
        }
        else {
            if ($dislikes / $attending > 0.2222121212){
                return true;
            }
            else{
                return false;
            }
        }

    }

?>