package com.ellixo.healthcare.domain;

import com.ellixo.healthcare.services.MedicamentService;
import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class WebScrapingTest {

    @Test
    public void scrap() throws IOException {
        // 64460520 60730566 63357706 66391261 60730566 60558554
        Document doc = Jsoup.connect("http://base-donnees-publique.medicaments.gouv.fr/extrait.php?specid=60558554").get();
        Elements elements = doc.select("h2.ficheInfo:contains(Indications thérapeutiques)");
        if (elements.size() != 0) {
            Element start = elements.first();
            Element end = doc.select("h2:gt(" + start.elementSiblingIndex() + ")").first();
            elements = doc.select("p:gt(" + start.elementSiblingIndex() + "):lt(" + end.elementSiblingIndex() + ")");

            StringBuilder sb = new StringBuilder();
            MedicamentService.MutableInteger listeCount = new MedicamentService.MutableInteger(0);
            elements.forEach(
                    x -> {
                        x.childNodes().forEach(
                                y -> {
                                    String string = getString(y);
                                    if (!Strings.isNullOrEmpty(string)) {
                                        String classParent = y.parentNode().attributes().get("class");
                                        if (classParent != null && classParent.startsWith("AmmListePuces")) {
                                            int length = "AmmListePuces".length();
                                            int index = Integer.parseInt(classParent.substring(length, length + 1));
                                            if (listeCount.getValue() > index) {
                                                sb.append("</ul>");
                                            } else if (listeCount.getValue() < index) {
                                                sb.append("<ul>");
                                            }
                                            listeCount.setValue(index);

                                            sb.append("<li>" + string + "</li>");
                                        } else {
                                            if (listeCount.getValue() != 0) {
                                                sb.append("</ul>");
                                            }
                                            listeCount.setValue(0);
                                            sb.append(string + " ");
                                        }
                                    }
                                });

                        if (sb.length() != 0) {
                            if (!sb.toString().endsWith(">")) {
                                sb.append("<br>");
                            }
                        }
                    }
            );
            for (int i = 0; i < listeCount.getValue(); i++) {
                sb.append("</ul>");
            }
            String text = sb.toString();

            if (text.endsWith("<br>")) {
                text = text.substring(0, text.length() - 4);
            }

            System.out.println(text);
        }
    }

    @Test
    public void scrapResume() throws IOException {
        // 64460520 60730566 63357706 66391261 60730566 60558554
        Document doc = Jsoup.connect("http://base-donnees-publique.medicaments.gouv.fr/affichageDoc.php?specid=64073783&typedoc=R").get();
        Elements elements = doc.select("h2.ficheInfo:contains(Indications thérapeutiques)");
        if (elements.size() != 0) {
            Element start = elements.first();
            Element end = doc.select("h2:gt(" + start.elementSiblingIndex() + ")").first();
            elements = doc.select("p:gt(" + start.elementSiblingIndex() + "):lt(" + end.elementSiblingIndex() + ")");

            StringBuilder sb = new StringBuilder();
            MedicamentService.MutableInteger listeCount = new MedicamentService.MutableInteger(0);
            elements.forEach(
                    x -> {
                        x.childNodes().forEach(
                                y -> {
                                    String string = getString(y);
                                    if (!Strings.isNullOrEmpty(string)) {
                                        String classParent = y.parentNode().attributes().get("class");
                                        if (classParent != null && classParent.startsWith("AmmListePuces")) {
                                            int length = "AmmListePuces".length();
                                            int index = Integer.parseInt(classParent.substring(length, length + 1));
                                            if (listeCount.getValue() > index) {
                                                sb.append("</ul>");
                                            } else if (listeCount.getValue() < index) {
                                                sb.append("<ul>");
                                            }
                                            listeCount.setValue(index);

                                            sb.append("<li>" + string + "</li>");
                                        } else {
                                            if (listeCount.getValue() != 0) {
                                                sb.append("</ul>");
                                            }
                                            listeCount.setValue(0);
                                            sb.append(string + " ");
                                        }
                                    }
                                });

                        if (sb.length() != 0) {
                            if (!sb.toString().endsWith(">")) {
                                sb.append("<br>");
                            }
                        }
                    }
            );
            for (int i = 0; i < listeCount.getValue(); i++) {
                sb.append("</ul>");
            }
            String text = sb.toString();

            if (text.endsWith("<br>")) {
                text = text.substring(0, text.length() - 4);
            }

            System.out.println(text);
        }
    }

    private String getString(Node node) {
        String string = node.toString().trim();
        if (string.length() > 1) {
            if ((node.nodeName().equals("a") && Strings.isNullOrEmpty(node.attributes().get("href")))
                    || (node.nodeName().equals("span"))) {
                return getString(node.childNode(0));
            } else {
                if (string.endsWith("·")) {
                    return null;
                } else {
                    return string;
                }
            }
        }
        return null;
    }

}
