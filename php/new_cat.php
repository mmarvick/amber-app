<?php
$cats = array("Bob", "Fred", "Morpheus", "Dilweed", "Miranda", "Michael", "Pikachu");
$bitch = array(true, false);
$files = scandir('pics');
$rand_cat_key = array_rand($cats, 1);
$rand_bitch_key = array_rand($bitch, 1);
$rand_file_key = 0;
while ($rand_file_key < 2) {
    $rand_file_key = array_rand($files,1);
}
$pic_url = 'http:&#47;&#47;www.mmarvick.com&#47;amber_app&#47;pics&#47;' . $files[$rand_file_key];
$output = array('name' => $cats[$rand_cat_key], 'bad' => $bitch[$rand_bitch_key], 'pic_url' => $pic_url );
echo json_encode($output);
?>