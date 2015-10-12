#### Inverted-Index
    - unix join - http://www.albany.edu/~ig4895/join.htm
    - get path-filenames - http://stackoverflow.com/questions/6844785/how-to-use-regex-with-find-command
    - executing unix commands from java
      - http://www.mkyong.com/java/how-to-execute-shell-command-from-java/
      - http://alvinalexander.com/java/edu/pj/pj010016
    - jsoup - http://jsoup.org/cookbook/input/load-document-from-file
    - frequency - http://www.tutorialspoint.com/java/util/collections_frequency.htm
    - Java caching system - https://commons.apache.org/proper/commons-jcs/
    - Unique Document ID : http://www.javapractices.com/topic/TopicAction.do?Id=56
    - BreakIterator : http://docs.oracle.com/javase/6/docs/api/java/text/BreakIterator.html
    - SequenceGenerator - http://codereview.stackexchange.com/questions/54641/thread-safe-integer-sequence
	- Guava - https://code.google.com/p/guava-libraries/wiki/NewCollectionTypesExplained#Multiset
	- FileUtils - https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FileUtils.html
	
	String encoding = "UTF-8";
int maxlines = 100;
BufferedReader reader = null;
BufferedWriter writer = null;

try {
    reader = new BufferedReader(new InputStreamReader(new FileInputStream("/bigfile.txt"), encoding));
    int count = 0;
    for (String line; (line = reader.readLine()) != null;) {
        if (count++ % maxlines == 0) {
            close(writer);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/smallfile" + (count / maxlines) + ".txt"), encoding));
        }
        writer.write(line);
        writer.newLine();
    }
} finally {
    close(writer);
    close(reader);
}

http://stackoverflow.com/questions/15530484/how-to-switch-between-two-thread-back-and-forth