package budikpet.cvut.cz.semestralwork.articles;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/**
 * Created by Petr on 15.03.18.
 * Class used for temporary storage of articles until RSS is implemented.
 */

public class DataStorage {
    private static Hashtable<String, Article> articles;
    private static final int MAX_ARTICLES = 10;

    public static void init() {
        articles = new Hashtable<>();
        /*for (int i = 0; i < MAX_ARTICLES; i++) {
            Article curr = new Article(i);
            curr.setAuthor(i + ". Author");
            curr.setHeading(i + ". Heading");
            curr.setText(i + ". Text of this Article");
            articles.put(i + "", curr);
        }*/

        /*

         */
        int i = 0;

        Article a0 = new Article(0, "Český herec s francouzským jménem le Breux hrál od mládí staříky",
                "Jeho jméno rozhodně nevypadá tuzemsky, přesto se stal uznávaným českým hercem. Felix le Breux se narodil před 100 lety, 5. dubna 1918 v Plzni. Byl hlavně divadelním hercem. Po válce působil třeba v divadle ABC, až nakonec zakotvil v Městských divadlech pražských.",
                "Vladimír Vokál",
                new GregorianCalendar(2018, 3, 5, 7, 0));
        try {
            a0.setUrl(new URL("https://technet.idnes.cz/le-breux-herec-film-divadlo-pred-100-lety-dyf-/pred-100-lety.aspx?c=A180404_175801_pred-100-lety_vov"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Article a1 = new Article(0, "Nahrajeme váš mozek na cloud, plánuje startup. Ale zabije vás tím",
                "Yesterday",
                "Tomáš Hegedüš",
                new GregorianCalendar(2018, 2, 20, 15, 8));
        try {
            a1.setUrl(new URL("https://technet.idnes.cz/nectome-cloud-obsah-mozku-mozek-synapse-smrt-michael-mccanna-robert-mcintyre-mit-kryoprezervace-imv-/kratke-zpravy.aspx?c=A180316_150816_tec-kratke-zpravy_hege"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Article a2 = new Article(0, "Nahrajeme váš mozek na cloud, plánuje startup. Ale zabije vás tím",
                "ThisWeek",
                "Tomáš Hegedüš",
                new GregorianCalendar(2018, 2, 17, 7, 0));
        try {
            a2.setUrl(new URL("https://technet.idnes.cz/nectome-cloud-obsah-mozku-mozek-synapse-smrt-michael-mccanna-robert-mcintyre-mit-kryoprezervace-imv-/kratke-zpravy.aspx?c=A180316_150816_tec-kratke-zpravy_hege"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Article a3 = new Article(0, "Nahrajeme váš mozek na cloud, plánuje startup. Ale zabije vás tím",
                "LastMonth",
                "Tomáš Hegedüš",
                new GregorianCalendar(2018, 0, 17, 15, 8));
        try {
            a3.setUrl(new URL("https://technet.idnes.cz/nectome-cloud-obsah-mozku-mozek-synapse-smrt-michael-mccanna-robert-mcintyre-mit-kryoprezervace-imv-/kratke-zpravy.aspx?c=A180316_150816_tec-kratke-zpravy_hege"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        articles.put(i++ + "", a0);
        articles.put(i++ + "", a1);
        articles.put(i++ + "", a2);
        articles.put(i++ + "", a3);

    }

    public static Hashtable<String, Article> getArticles() {
        return articles;
    }

    public static Article getArticle(int id) {
        return articles.get(id + "");
    }

}
