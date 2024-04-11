<?php
try {
	$pdoSet = new PDO('mysql:host=localhost', 'root', '');
	$pdoSet->query('USE test;SET NAMES utf8;');
} catch (PDOException $e) {
	print "Error!: " . $e->getMessage() . "<br />";
	die();
}

$socket = stream_socket_server("tcp://0.0.0.0:8081", $errno, $errstr);
if (!$socket)
    die("$errstr ($errno)\n");

echo "Service running at http://localhost:8081/ ... \n";
while ($connect = stream_socket_accept($socket, -1)) {
    fwrite($connect, "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n");

	echo "Generation HTML...\n";
	$sOut="<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\"></head><body>";
	$sOut.="<style>html{font-family: sans-serif;} table {width: 100%;border-collapse: collapse;} td {border: 1px solid black;padding: 3px;} tr:hover {background: #d0e3f7;} p {font-size: large;font-weight: bold;}</style>";
	$sOut.="<p>Применённые технологии: PHP + MySQL.</p>";
			
	$stmt=$pdoSet->query("SELECT * FROM myarttable WHERE id>14 ORDER BY id DESC");
	$resultMF = $stmt->fetchAll(PDO::FETCH_NUM);
	$sOut.="<table>";
	for ($iRow=0; $iRow<Count($resultMF); ++$iRow) {
		$sOut.="<tr>";
		for ($iCol=0; $iCol<Count($resultMF[$iRow]); ++$iCol)
			$sOut.="<td>".$resultMF[$iRow][$iCol]."</td>";
		$sOut.="</tr>";
	}
	$sOut.="</table>";
	echo "DB is ok.\n";

	$sOut.="</body></html>";
	echo "HTML is complete.\n";
    fwrite($connect, $sOut);
    fclose($connect);
}
fclose($socket);
