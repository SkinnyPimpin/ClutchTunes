<?php
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    date_default_timezone_set('EST');
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables
    $partyTitle = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
    $videoTitle = mysqli_real_escape_string($connect, $_POST["videoTitle"]);
    $username = mysqli_real_escape_string($connect, $_POST["username"]);

    // $partyTitle = "tomorrow_is_capstone";
    // $videoTitle = "A\$AP Rocky LVL";

    $response = array();
    $response["likes"] = 0;
    $response["dislikes"] = 0;
    $response["thumbnailURL"] = "";
    $response["videoLink"] = "";
    $response["userLoves"] = false;

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

    $stmt = "SELECT thumbnailURL FROM " .$partyTitle. " WHERE title = ?";
    //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
    $statement = mysqli_prepare($connect, $stmt);
    mysqli_stmt_bind_param($statement, "s", $videoTitle);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colThumbnailURL);
    while(mysqli_stmt_fetch($statement)){
        $response["thumbnailURL"] = $colThumbnailURL;
    }   

    $stmt = "SELECT link FROM " .$partyTitle. " WHERE title = ?";
    //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
    $statement = mysqli_prepare($connect, $stmt);
    mysqli_stmt_bind_param($statement, "s", $videoTitle);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colLink);
    while(mysqli_stmt_fetch($statement)){
        $response["videoLink"] = $colLink;
    }  

    $stmt = "SELECT * FROM " .$username. " WHERE title = ?";
    //$statement = mysqli_prepare($connect, "SELECT link FROM likew WHERE title = ?");
    $statement = mysqli_prepare($connect, $stmt);
    mysqli_stmt_bind_param($statement, "s", $videoTitle);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    $count = mysqli_affected_rows($connect);
    mysqli_stmt_close($statement); 
    if ( $count >= 1){ 
        $response["userLoves"] = true;
    }


    echo json_encode($response);

?>