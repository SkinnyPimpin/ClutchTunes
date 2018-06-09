<?php

//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");

    //$partyTitle = "bennetts";
    $username = mysqli_real_escape_string($connect, $_POST["username"]);

    
    // $stmt = "SELECT * FROM ". $partyTitle . " ORDER BY likes DESC";
    // $statement = mysql_query($connect, "SELECT * FROM '$partyTitle' ORDER BY likes DESC");
    //$stmt = "SELECT * FROM " .$partyTitle. " WHERE played = '$zero'";
    // $query = "SELECT * FROM ". $partyTitle . " ORDER BY likes DESC";

    // $statement = mysqli_query($connect, $query);
    // mysqli_stmt_execute($statement);
    //mysqli_stmt_store_result($statement);
    //mysqli_stmt_bind_result($statement, $colID, $colTitle, $colLink, $colLikes, $colDislikes, $colPlayed);

        $stmt = "SELECT * FROM " .$username;
        //$statement = mysqli_prepare($connect, "UPDATE '$title' SET played = '1' WHERE link = '$songID'");
        $statement = mysqli_prepare($connect, $stmt);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $colID, $colTitle, $colLink, $colThumbnailURL);

    $flag = array();
    while(mysqli_stmt_fetch($statement)){
        $flag[] = $colTitle;        
    }
    print(json_encode($flag));
?>


