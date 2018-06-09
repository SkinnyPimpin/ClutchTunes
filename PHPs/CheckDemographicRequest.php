<?php
//Require password.php enables the function password_hash() to be called
    require("password.php");
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    date_default_timezone_set('EST');
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables
    $partyTitle = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
    $partyPassword = mysqli_real_escape_string($connect, $_POST["partyPassword"]);
	$partyDemographics = mysqli_real_escape_string($connect, $_POST["partyDemographics"]);
	$partyRadius = mysqli_real_escape_string($connect, $_POST["partyRadius"]);
	$partyTimeframe = mysqli_real_escape_string($connect, $_POST["partyTimeframe"]);
    $username = mysqli_real_escape_string($connect, $_POST["username"]);


	// $partyTitle = "marjdin";
	// $partyPassword = "password";
	// $partyDemographics = "18+";
	// $partyRadius = "25ft";
	// $partyTimeframe = "NO";
 //    $username = "bennett";

    $statement = mysqli_prepare($connect, "SELECT * FROM Users WHERE username = ?");
    mysqli_stmt_bind_param($statement, "s", $username);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colUserID, $colUsername, $colPassword, $colFN, $colEmail, $colHash, $colActive, $colDOB, $colBiography);

    $response = array();
    $response["userIs18"] = true;

    while(mysqli_stmt_fetch($statement)){
        $month = substr($colDOB, 0, -7);
        $day = substr($colDOB, 3, -5);
        $year = substr($colDOB, 6);
        
        $stampBirth = mktime(0, 0, 0, $month, $day, $year);

        $today['day'] = date('d');
        $today['month'] = date('m');
        $today['year'] = date('y') - 18;

        $stampToday = mktime(0, 0, 0, $today['month'], $today['day'], $today['year']);

        if (!($stampBirth < $stampToday)) {
            $response["userIs18"] = false;
            break;
        } 
    }
//Echos response
    echo json_encode($response);
?>