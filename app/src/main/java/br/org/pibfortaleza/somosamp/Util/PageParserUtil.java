package br.org.pibfortaleza.somosamp.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import br.org.pibfortaleza.somosamp.model.Autor;
import br.org.pibfortaleza.somosamp.model.Post;

/**
 * Created by Ely on 03/02/17.
 */

public class PageParserUtil {

    public static ArrayList<Autor> getAutorLinks(String url) {

        Document doc = null;

        try {

            doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);

            Elements autorTitles = doc.getElementsByClass("one-half author-info");

            ArrayList<Autor> autores = new ArrayList<>();

            Autor autor;

            for (Element row : autorTitles) {

                autor = new Autor();

                Elements hElements = row.getElementsByTag("h4");

                Element hElement = hElements.get(0);

                Elements aElements = hElement.getElementsByTag("a");

                Element aElement = aElements.get(0);

                autor.setUrl(aElement.attr("abs:href"));
                autor.setNome(aElement.ownText());

                hElements = row.getElementsByTag("h5");

                hElement = hElements.get(0);

                aElements = hElement.getElementsByTag("a");

                aElement = aElements.get(0);

                String qtdPost = aElement.ownText();

                qtdPost = qtdPost.replace("posts", "");

                qtdPost = qtdPost.replace("post", "");

                autor.setNumPosts(Integer.valueOf(qtdPost.trim()));

                autores.add(autor);

            }

            return autores;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<String> getPostLinks(String url) {

        Document doc = null;

        try {

            doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);

            Elements postTitles = doc.getElementsByClass("post-title entry-title");

            ArrayList<String> postLinks = new ArrayList<>();

            for (Element row : postTitles) {

                Elements elements = row.getElementsByTag("a");

                Element aElement = elements.get(0);

                postLinks.add(aElement.attr("abs:href"));

            }

            return postLinks;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Post getPostFromUrl(String url) {


        Document doc = null;

        try {

            doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);

            Post post = new Post();

            post.setData(doc.select("time").first().attr("datetime"));

            post.setDataDescricao(doc.select("time").first().text());

            post.setTitulo(doc.select("h2").first().text());

            post.setCategoria(doc.getElementsByClass("post-categories").first().text());

            post.setAutor(doc.getElementsByClass("post-author-content").first().select("h4").text());

            post.setPostUrl(url);

            // Recupera container onde contem o Texto do post
            Elements textElements = doc.getElementsByClass("post-content clear");

            ArrayList<String> texto = new ArrayList<>();

            // Flag para verificar se post tem video do youtube
            int html_split_div_index = 0;

            Elements youtubeElements = textElements.first().getElementsByClass("jetpack-video-wrapper");

            Elements meta = doc.getElementsByTag("meta").attr("name", "description");

            Elements metas = meta.select("[content]");

            for (Element m : metas) {

                if (m.attr("content").contains("https://www.youtube.com/watch?v=")) {

                    String videoId = (m.attr("content").split(" ")[0]).split("https://www.youtube.com/watch?v=")[0];

                    videoId = videoId.split("&")[0];

                    videoId = videoId.split("v=")[1];

                    post.setYoutubeUrl(videoId);

                    post.setImagemUrl("http://img.youtube.com/vi/" + videoId + "/hqdefault.jpg");

                    //
/*
                    post.setYoutubeUrl(m.attr("content").split(" ")[0]);

                    String videoId = post.getYoutubeUrl().split("https://www.youtube.com/watch?v=")[0];

                    videoId = videoId.split("&")[0];

                    videoId = videoId.split("v=")[1];

                    post.setYoutubeUrl(videoId);

                    videoId = "http://img.youtube.com/vi/"+videoId+"/hqdefault.jpg";

                    post.setImagemUrl(videoId);*/

                }

            }

            if (doc.getElementsByClass("attachment-baskerville-post-image size-baskerville-post-image wp-post-image").first() != null) {
                post.setImagemUrl(doc.getElementsByClass("attachment-baskerville-post-image size-baskerville-post-image wp-post-image").first().attr("src"));
            } else {

                //
                //System.out.println("s");
            }

            if (youtubeElements != null) {
                if (youtubeElements.size() > 0) {
                    html_split_div_index = 1;
                }
            } else {
                post.setYoutubeUrl("");
            }

            String html = textElements.first().html();

            // Divide html em div's
            String[] parts = html.split("<div");

            // Recupera a div que possui o corpo do Texto do post
            html = parts[html_split_div_index];

            // Retira corpor e tag de style do html
            html = html.split("<style type=\"text/css\">")[0];

            // Retira corpor e tag do youtube do html
            if (html.contains("</div>"))
                html = html.split("</div>")[1];

            post.setTexto(html);

            return post;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
