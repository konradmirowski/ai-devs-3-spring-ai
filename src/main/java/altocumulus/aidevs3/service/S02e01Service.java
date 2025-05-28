package altocumulus.aidevs3.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.Whisper1Client;
import altocumulus.aidevs3.client.openai.text.GptModel;
import altocumulus.aidevs3.client.openai.text.TextClient;
import altocumulus.aidevs3.model.common.ApiRequest;
import altocumulus.aidevs3.model.s02e01.AudioFile;

@Service
public class S02e01Service {

    // private static final String SYSTEM_PROMPT_1 =
    //     "You are an information extraction and summarization assistant. " +
    //     "You will be provided with text concerning Andrzej Maj. " +
    //     "Your task is to analyze this text and prepare a summary. " +
    //     "Focus specifically on identifying and detailing information related to his place(s) of work or employment. " +
    //     "This includes the name of the institution(s), its location if mentioned, his role, and any other relevant details about his professional engagement.";
    
    private static final String SYSTEM_PROMPT_2 =
        "You are a highly specialized information extraction and research assistant. " +
        "You will be provided with a summary of information about Andrzej Maj, focusing on his workplace.\n\n" +
        "Your tasks are as follows:\n" +
        "1. Identify Andrzej Maj's *current* and *exact* place of work from the provided summary.\n" +
        "2. If the address of this current workplace is present in the summary, use that address.\n" +
        "3. If the address of this current workplace is *not* present in the summary, " +
        "use your own knowledge and resources to find the address of the identified current workplace.\n\n" +
        "Once you have the address (either from the summary or your own research):\n" +
        "Extract *only* the street name. Your response must be *only* the street name itself. " +
        "Do not include building numbers, city names, postal codes, or prefixes such as 'Street', 'ul.', 'Ulica', 'Aleja', etc. " +
        "For example, if the address is 'Ulica Długa 7, Kraków', your output should be 'Długa'. " +
        "If the address is 'Aleja Niepodległości 100', your output should be 'Niepodległości'. " +
        "Do not add any other words, phrases, or explanations before or after the street name.\n\n" +
        "If the current workplace cannot be identified from the summary, " +
        "or if you cannot find an address for the identified workplace (neither in the summary nor through your own research), " +
        "or if the street name cannot be definitively extracted, " +
        "your response must be *only* the phrase: 'Street name not found'. " +
        "Do not add any other words, phrases, or explanations.";

    private final C3ntralaClient c3ntralaClient;
    private final TextClient textClient;
    private final Whisper1Client whisper1Client;
    private final List<AudioFile> audioFiles = List.of(
        new AudioFile("static/s02e01/adam.m4a", "adam.m4a", "audio/mp4"),
        new AudioFile("static/s02e01/agnieszka.m4a", "agnieszka.m4a", "audio/mp4"),
        new AudioFile("static/s02e01/ardian.m4a", "ardian.m4a", "audio/mp4"),
        new AudioFile("static/s02e01/michal.m4a", "michal.m4a", "audio/mp4"),
        new AudioFile("static/s02e01/monika.m4a", "monika.m4a", "audio/mp4"),
        new AudioFile("static/s02e01/rafal.m4a", "rafal.m4a", "audio/mp4")
    );

    @Value("${c3ntrala.api.key}")
    private String apiKey;
 
    public S02e01Service(C3ntralaClient c3ntralaClient, TextClient textClient, Whisper1Client whisper1Client) {
        this.c3ntralaClient = c3ntralaClient;
        this.textClient = textClient;
        this.whisper1Client = whisper1Client;
    }

    public String getFlag() {
        List<String> transcibedAudioFiles = transcribeAudioFiles();

        // List<String> summaryList = transcibedAudioFiles.stream()
        //     .map(transcibedAudioFile -> chatService.askAI(transcibedAudioFile, SYSTEM_PROMPT_1, GptModel.GPT_4_1))
        //     .collect(Collectors.toList());
        // System.out.println("\n\nsummaryList: " + summaryList); //TODO: remove

        // String summarizedData = summaryList.stream()
        //     .map(s -> s + "\n\n")
        //     .collect(Collectors.joining());
        // System.out.println("\n\nSummarized data: " + summarizedData); //TODO: remove

        String summarizedData = transcibedAudioFiles.stream()
            .map(s -> s + "\n\n")
            .collect(Collectors.joining());
        System.out.println("\n\nSummarized data: " + summarizedData); //TODO: remove

        String streetName = textClient.askAI(summarizedData, SYSTEM_PROMPT_2, GptModel.GPT_4_1);
        System.out.println("\n\nAI answer: " + streetName); //TODO: remove
        
        return sendDataToApi(streetName);
    }

    private List<String> transcribeAudioFiles() {
        return audioFiles.stream()
            .map(audioFile -> {
                try {
                    MultipartFile multipartFile = getMultipartFileFromClasspath(
                        audioFile.classpathFilePath(),
                        audioFile.originalFilename(),
                        audioFile.contentType()
                    );
                    return whisper1Client.transcribeAudio(multipartFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error processing file: " + audioFile.originalFilename();
                }
            })
            .toList();
    }

    public MultipartFile getMultipartFileFromClasspath(String classpathFilePath, String originalFilename, String contentType) throws Exception {
        Resource resource = new ClassPathResource(classpathFilePath);

        if (!resource.exists()) {
            throw new IOException("File not found at classpath: " + classpathFilePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] content = inputStream.readAllBytes();
            return new MockMultipartFile(
                "file",  // This should be "file" as expected by OpenAI API
                originalFilename,
                contentType,
                content
            );
        }
    }

    private String sendDataToApi(String streetName) {
        ApiRequest request = new ApiRequest("mp3", apiKey, streetName);
        return c3ntralaClient.sendPost(request);
    }


        //transcribed audio, for faster testing, remove later
        // private static final String summarizedData2 =
        // "Summarized data: Andrzej Maj? No coś kojarzę. Był taki gość. Pamiętam. " +
        // "Pracował u nas w biurze. Był project managerem. Chociaż, moment, może to jednak był Arkadiusz Maj? " +
        // "Też na literę A. Mógłbym się pomylić. No jednak tak, Arkadiusz. Z Arkadiuszem współpracowałem w Wałbrzychu. " +
        // "Pamiętam, że był naprawdę wrednym facetem. Normalnie nie chciałbyś z takim pracować. " +
        // "Jak coś było do zrobienia, to albo stosował typową spychologię, albo zamiatał sprawę pod dywan. " +
        // "Nigdy człowieka nie docenił. Wszystkie zasługi brał na siebie. Był naprawdę beznadziejny. " +
        // "Arkadiusza pamiętam jak dziś, więc jeśli chcecie go aresztować, to jak najbardziej. Jestem za. " +
        // "Takich ludzi powinno się zamykać, a nie mnie, bo ja jestem niewinny. Jak chcecie, to ja wam mogę adres nawet podać. " +
        // "Stefana Batorego, 68D. Tylko D jak Danuta, bo pod B mieszka jego ciocia, a ona była fajna. " +
        // "Jak będziecie Arkadiusza aresztować, to proszę powiedzcie mu z pozdrowieniami od Adama. A on będzie wiedział, o kogo chodzi.\n\n" +
        // "Może go znałam, a może nie. Kto wie? Zacznijmy od tego, że nie macie prawa mnie tutaj przetrzymywać. " +
        // "Absolutnie nic złego nie zrobiłam. Trzymacie mnie tutaj niezgodnie z prawem. " +
        // "Wiem, że teraz wszystko się zmienia na świecie i roboty dyktują, jak ma być, ale o ile się nie mylę, dawne prawo nadal obowiązuje. " +
        // "Mamy tutaj jakąś konstytucję, prawda? Chcę rozmawiać z adwokatem. " +
        // "Maja znałam, to prawda. Było to kilka lat temu. Pracowaliśmy razem w Warszawie, ale na tym nasza znajomość się skończyła. " +
        // "Byliśmy w tej samej pracy. Czy to jest jakieś przestępstwo? To jest coś niedozwolonego w naszym kraju? " +
        // "Za to można wsadzać ludzi do więzienia? On wjechał z Warszawy, nie ma go tam. " +
        // "Z tego, co wiem, pojechał do Krakowa. Wykładać tam chciał chyba coś z informatyki czy matematyki. " +
        // "Nie wiem, jak to się skończyło. Może to były tylko plany?\n\n" +
        // "No pewnie. Obserwowałem jego dokonania i muszę przyznać, że zrobił na mnie wrażenie. " +
        // "Ja mam taką pamięć opartą na wrażeniach. I wrażenie mi pozostało po pierwszym spotkaniu. " +
        // "Nie wiem kiedy to było, ale on był taki... taki nietypowy. " +
        // "Później zresztą zastanawiałem się, jak to jest możliwe, że robi tak wiele rzeczy. Nieprzeciętny, ale swój. " +
        // "Znany w końcu to Andrzej. Naukowiec. Później chyba zniknął z miejsc, gdzie go śledziłem. " +
        // "Przy okazji jakiejś konferencji czy eventu chyba widziałem go, ale nie udało mi się z nim porozmawiać. " +
        // "Nie, nie mamy żadnego kontaktu. Nie jest moją rodziną, więc dlaczego miałbym ukrywać? Ja go tylko obserwowałem. " +
        // "Różnych ludzi się obserwuje. To nie zbrodnia, prawda? Kiedy w końcu zostawicie mnie w spokoju?\n\n" +
        // "Gość miał ambicje, znałem go w sumie od dzieciństwa, w zasadzie to znałem, bo trochę nam się kontakt urwał, " +
        // "ale jak najbardziej, pracowaliśmy razem. On zawsze chciał pracować na jakiejś znanej uczelni, " +
        // "po studiach pamiętam, został na uczelni i robił doktorat z sieci neuronowych i uczenia maszynowego, " +
        // "potem przeniósł się na inną uczelnię i pracował chwilę w Warszawie, ale to był tylko epizod z Warszawy. " +
        // "On zawsze mówił, że zawsze musi pracować na jakiejś ważnej uczelni, bo w tym środowisku bufonów naukowych to się prestiż liczy. " +
        // "Mówił, królewska uczelnia, to jest to, co chce osiągnąć, na tym mu zależało. " +
        // "Mówił, ja się tam dostanę, zobaczysz, no i będę tam wykładał. Z tego co wiem, no to osiągnął swój cel, no i brawa dla niego. " +
        // "Lubię ludzi, którzy jak się uprzą, że coś zrobią, to po prostu to zrobią, ale to nie było łatwe, " +
        // "ale gościowi się udało i to wcale nie metodą po trupach do celu. " +
        // "Andrzej był okej, szanował ludzi, marzył o tej uczelni i z tego co wiem, to na niej wylądował. " +
        // "Nie miałem z nim już kontaktu, ale widziałem, że profil na Linkedin zaktualizował. " +
        // "Nie powiedzieliście mi, dlaczego go szukacie, bo praca na uczelni to nie jest coś zabronionego, prawda? " +
        // "A, z rzeczy ważnych, to chciałbym wiedzieć, dlaczego jestem tu, gdzie jestem i kiedy się skończy to przesłuchanie. " +
        // "Dostaję pytania chyba od dwóch godzin i w sumie powiedziałem już wszystko, co wiem.\n\n" +
        // "Ale wy tak na serio pytacie? Bo nie znać Andrzeja Maja w naszych kręgach to naprawdę byłoby dziwne. " +
        // "Tak, znam go. Podobnie jak pewnie kilka tysięcy innych uczonych go zna. Andrzej pracował z sieciami neuronowymi. To prawda. " +
        // "Był wykładowcą w Krakowie. To także prawda. Z tego co wiem, jeszcze przynajmniej pół roku temu tam pracował. " +
        // "Wydział czy tam Instytut Informatyki i Matematyki Komputerowej czy jakoś tak. " +
        // "Nie pamiętam jak się to dokładnie teraz nazywa, ale w każdym razie gość pracował z komputerami i sieciami neuronowymi. " +
        // "No chyba jesteście w stanie skojarzyć fakty. Nie? Komputery, sieci neuronowe? To się łączy. " +
        // "Bezpośrednio z nim nie miałam kontaktu. Może raz na jakimś sympozium naukowym pogratulowałam mu świetnego wykładu, " +
        // "ale to wszystko co nas łączyło. Jeden uścisk dłoni, nigdy nie weszliśmy do wspólnego projektu, nigdy nie korespondowałam z nim. " +
        // "Tak naprawdę znam go jako celebrytę ze świata nauki, ale to wszystko co mogę wam powiedzieć.\n\n" +
        // "Andrzejek, Andrzejek! Myślę, że osiągnął to co chciał. Jagiełło byłby z niego very dumny. " +
        // "Chociaż, nie wiem, może coś mi się myli. Jagiełło chyba nie był jego kolegą i raczej nie miał z tą uczelnią wiele wspólnego. " +
        // "To tylko nazwa. Taka nazwa. To był jakiś wielki gość, bardziej co ją założył. Ale co to ma do rzeczy? " +
        // "Ale czy Andrzejek go znał? Chyba nie, ale nie wiem, bo Andrzejek raczej nie żył w czternastym wieku. " +
        // "Kto go tam wie? Mógł odwiedzić czternasty wiek. Ja bym odwiedził. Tego instytutu i tak wtedy nie było. To nowe coś. " +
        // "Ta ulica od matematyka co wpada w komendanta to chyba dwudziesty wiek. Ten czas mi się miesza. " +
        // "Wszystko jest takie nowe. To jest nowy, lepszy świat. Podoba ci się świat, w którym żyjesz? " +
        // "Andrzej zawsze był dziwny, kombinował coś i mówił, że podróże w czasie są możliwe. " +
        // "Razem pracowaliśmy nad tymi podróżami. To wszystko, co teraz się dzieje i ten stan, w którym jestem, " +
        // "to jest wina tych wszystkich podróży, tych tematów, tych rozmów. " +
        // "Ostatecznie nie wiem, czy Andrzejek miał rację i czy takie podróże są możliwe. " +
        // "Jeśli kiedykolwiek spotkacie takiego podróżnika, dajcie mi znać. Proszę, to by oznaczało, że jednak nie jestem szalony, " +
        // "ale jeśli taki ktoś wróci w czasie i pojawi się akurat dziś, to by znaczyło, że ludzie są zagrożeni. Jesteśmy zagrożeni. " +
        // "Andrzej jest zagrożony. Andrzej nie jest zagrożony. Andrzej jest zagrożony. " +
        // "Ale jeśli ktoś wróci w czasie i pojawi się akurat dziś, to by znaczyło, że ludzie są zagrożeni. " +
        // "Jesteśmy zagrożeni. Andrzej jest zagrożony. Andrzej nie jest zagrożony. To Andrzej jest zagrożeniem. " +
        // "To Andrzej jest zagrożeniem. Andrzej nie jest zagrożony. Andrzej jest zagrożeniem.";
}
