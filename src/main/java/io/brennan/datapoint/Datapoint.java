package io.brennan.datapoint;

public class Datapoint {
  private String title;
  private Integer year;

  public Datapoint(String title, Integer year) {
    this.title = title;
    this.year = year;
  }

  public String getTitle() {
    return title;
  }

  public Integer getYear() {
    return year;
  }

  public static Datapoint getDatapointFromLine(String line){
      String[] splitLine = line.split("\t");
      try {
        String[] splitTitle = splitLine[1].split(" \\(");
        String title = splitTitle[0].replace("\"", "");
        if (title.contains(",")) {
          String[] commaTitle = title.split(",");

          title = commaTitle[1].substring(1) + " " + commaTitle[0];
        }

        if (splitTitle.length > 1) {
          String yearStr = splitTitle[splitTitle.length - 1].replace(")", "").replace("\"", "").replace(" ", "");
          try {
            Integer year = Integer.parseInt(yearStr);
            return new Datapoint(title, year);
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
        }
//        else {
//          System.err.println(splitLine[1]);
//        }
      } catch (ArrayIndexOutOfBoundsException e){
        System.err.println(line);
      }
      return null;
  }
}
