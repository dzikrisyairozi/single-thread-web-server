package com.serversocket;

import java.util.ArrayList;
import java.util.HashMap;

public class ListBuilder {
    private ArrayList<HashMap<String, String>> files;
    private String urn;
    private StringBuilder html;

    private static final String[] SIZE_SYMBOL_ORDER = {"B", "KB", "MB", "GB"};

    public ListBuilder(ArrayList<HashMap<String, String>> files, String urn) {
        this.files = files;
        this.urn = "/" + urn;

        generateHtml();
    }

    private void generateHtml() {
        html = new StringBuilder();
        html.append("<html>\n");
        html.append("<head>\n");
        html.append(String.format("<title>Index of %s</title>\n", urn));
        html.append("</head>\n");
        html.append("<body>\n");
        html.append(String.format("<h1>Index of %s</h1>\n", urn));
        html.append("<table>\n");
        html.append("<tbody>\n");

        html.append(getTableRows());

        html.append("</tbody>\n");
        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>\n");
    }

    private StringBuilder getTableRows() {
        StringBuilder tableRows = new StringBuilder();
        tableRows.append("           <tr>\n");
        tableRows.append("               <th valign=\"top\"><img src=\"/list/blank.gif\" alt=\"[ICO]\"></th>\n");
        tableRows.append("               <th>Name</th>\n");
        tableRows.append("               <th style=\"padding: 0 10px;\">Last modified</th>\n");
        tableRows.append("               <th>Size</th>\n");
        tableRows.append("           </tr>\n");
        tableRows.append("           <tr>\n");
        tableRows.append("               <th colspan=\"5\"><hr></th>\n");
        tableRows.append("           </tr>\n");

        for (HashMap<String, String> file : files) {
            boolean isFile = file.get("type").equals("file");
            String alt = (isFile) ? "TXT" : "DIR";
            String icon = (isFile) ? "text.gif" : "folder.gif";
            String iconPath = String.format("/list/%s", icon);

            // Get displayed size.
            String size = "-";
            if (!file.get("size").equals("0")) {
                size = getSize(file.get("size"));
            }

            tableRows.append(String.format("           <tr>\n"));
            tableRows.append(String.format("               <td valign=\"top\"><img src=\"%s\" alt=\"[%s]\"></td>\n", iconPath, alt));
            tableRows.append(String.format("               <td><a href=\"%s\">%s</a></td>\n", file.get("path"), file.get("name")));
            tableRows.append(String.format("               <td style=\"padding: 0 10px;\">%s\t</td>\n", file.get("lastModified")));
            tableRows.append(String.format("               <td align=\"right\">%s</td>\n", size));
            tableRows.append(String.format("           </tr>\n"));
        }

        tableRows.append("           <tr>\n");
        tableRows.append("               <th colspan=\"5\"><hr></th>\n");
        tableRows.append("           </tr>\n");

        return tableRows;
    }


    private String getSize(String sizeStr) {
        int symbolIdx = 0;
        int size = Integer.parseInt(sizeStr);

        while (symbolIdx < SIZE_SYMBOL_ORDER.length && ((size / 1024) > 0)) {
            size /= 1024;
            symbolIdx++;
        }
        return size + " " + SIZE_SYMBOL_ORDER[symbolIdx];
    }

    public String getHtml() {
        return html.toString();
    }
}
