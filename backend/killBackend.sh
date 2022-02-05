result=$(screen -ls | grep -o '[0-9]*\.backend')
for i in $result;
do
  echo $i;
  screen -X -S $i quit;
done
