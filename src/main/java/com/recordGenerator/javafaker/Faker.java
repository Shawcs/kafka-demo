package com.recordGenerator.javafaker;

import com.recordGenerator.javafaker.service.FakeValuesService;

import java.util.Locale;
import java.util.Random;

/**
 * Provides utility methods for generating fake strings, such as names, phone
 * numbers, addresses. generate random strings with given patterns
 *
 * @author ren
 */
public class Faker {
    private final com.recordGenerator.javafaker.service.RandomService randomService;
    private final FakeValuesService fakeValuesService;

    private final Ancient ancient;
    private final App app;
    private final Artist artist;
    private final com.recordGenerator.javafaker.Lorem lorem;
    private final Music music;
    private final Name name;
    private final Number number;
    private final Internet internet;
    private final PhoneNumber phoneNumber;
    private final Pokemon pokemon;
    private final Address address;
    private final Business business;
    private final com.recordGenerator.javafaker.Book book;
    private final ChuckNorris chuckNorris;
    private final Color color;
    private final Commerce commerce;
    private final com.recordGenerator.javafaker.Company company;
    private final com.recordGenerator.javafaker.Crypto crypto;
    private final IdNumber idNumber;
    private final com.recordGenerator.javafaker.Hacker hacker;
    private final Options options;
    private final Code code;
    private final Finance finance;
    private final Food food;
    private final GameOfThrones gameOfThrones;
    private final DateAndTime dateAndTime;
    private final Demographic demographic;
    private final Educator educator;
    private final Shakespeare shakespeare;
    private final SlackEmoji slackEmoji;
    private final Space space;
    private final com.recordGenerator.javafaker.Superhero superhero;
    private final Bool bool;
    private final Team team;
    private final Beer beer;
    private final com.recordGenerator.javafaker.University university;
    private final Cat cat;
    private final File file;
    private final Stock stock;

    public Faker() {
        this(Locale.ENGLISH);
    }

    public Faker(Locale locale) {
        this(locale, null);
    }

    public Faker(Random random) {
        this(Locale.ENGLISH, random);
    }

    public Faker(Locale locale, Random random) {
        this.randomService = new com.recordGenerator.javafaker.service.RandomService(random);
        this.fakeValuesService = new FakeValuesService(locale, randomService);

        this.ancient = new Ancient(this);
        this.app = new App(this);
        this.artist = new Artist(this);
        this.lorem = new com.recordGenerator.javafaker.Lorem(this);
        this.music = new Music(this);
        this.name = new Name(this);
        this.number = new Number(this);
        this.internet = new Internet(this);
        this.phoneNumber = new PhoneNumber(this);
        this.pokemon = new Pokemon(this);
        this.address = new Address(this);
        this.book = new com.recordGenerator.javafaker.Book(this);
        this.business = new Business(this);
        this.chuckNorris = new ChuckNorris(this);
        this.color = new Color(this);
        this.idNumber = new IdNumber(this);
        this.hacker = new com.recordGenerator.javafaker.Hacker(this);
        this.company = new com.recordGenerator.javafaker.Company(this);
        this.crypto = new com.recordGenerator.javafaker.Crypto(this);
        this.commerce = new Commerce(this);
        this.options = new Options(this);
        this.code = new Code(this);
        this.file = new File(this);
        this.finance = new Finance(this);
        this.food = new Food(this);
        this.gameOfThrones = new GameOfThrones(this);
        this.dateAndTime = new DateAndTime(this);
        this.demographic = new Demographic(this);
        this.educator = new Educator(this);
        this.shakespeare = new Shakespeare(this);
        this.slackEmoji = new SlackEmoji(this);
        this.space = new Space(this);
        this.superhero = new com.recordGenerator.javafaker.Superhero(this);
        this.team = new Team(this);
        this.bool = new Bool(this);
        this.beer = new Beer(this);
        this.university = new com.recordGenerator.javafaker.University(this);
        this.cat = new Cat(this);
        this.stock = new Stock(this);
    }

    /**
     * Returns a string with the '#' characters in the parameter replaced with random digits between 0-9 inclusive.
     * <p>
     * For example, the string "ABC##EFG" could be replaced with a string like "ABC99EFG".
     *
     * @param numberString
     * @return
     */
    public String numerify(String numberString) {
        return fakeValuesService.numerify(numberString);
    }

    /**
     * Returns a string with the '?' characters in the parameter replaced with random alphabetic
     * characters.
     * <p>
     * For example, the string "12??34" could be replaced with a string like "12AB34".
     *
     * @param letterString
     * @return
     */
    public String letterify(String letterString) {
        return fakeValuesService.letterify(letterString);
    }

    /**
     * Returns a string with the '?' characters in the parameter replaced with random alphabetic
     * characters.
     * <p>
     * For example, the string "12??34" could be replaced with a string like "12AB34".
     *
     * @param letterString
     * @param isUpper
     * @return
     */
    public String letterify(String letterString, boolean isUpper) {
        return fakeValuesService.letterify(letterString, isUpper);
    }

    /**
     * Applies both a {@link #numerify(String)} and a {@link #letterify(String)}
     * over the incoming string.
     *
     * @param string
     * @return
     */
    public String bothify(String string) {
        return fakeValuesService.bothify(string);
    }

    /**
     * Applies both a {@link #numerify(String)} and a {@link #letterify(String)}
     * over the incoming string.
     *
     * @param string
     * @param isUpper
     * @return
     */
    public String bothify(String string, boolean isUpper) {
        return fakeValuesService.bothify(string, isUpper);
    }

    /**
     * Generates a String that matches the given regular expression.
     */
    public String regexify(String regex) {
        return fakeValuesService.regexify(regex);
    }

    public com.recordGenerator.javafaker.service.RandomService random() {
        return this.randomService;
    }

    FakeValuesService fakeValuesService() {
        return this.fakeValuesService;
    }

    public Ancient ancient() {
        return ancient;
    }

    public App app() {
        return app;
    }

    public Artist artist() {
        return artist;
    }

    public Music music() {
        return music;
    }

    public Name name() {
        return name;
    }

    public Number number() {
        return number;
    }

    public Internet internet() {
        return internet;
    }

    public PhoneNumber phoneNumber() {
        return phoneNumber;
    }

    public Pokemon pokemon() {
        return pokemon;
    }

    public com.recordGenerator.javafaker.Lorem lorem() {
        return lorem;
    }

    public Address address() {
        return address;
    }

    public com.recordGenerator.javafaker.Book book() {
        return book;
    }

    public Business business() {
        return business;
    }

    public ChuckNorris chuckNorris() {
        return chuckNorris;
    }

    public Color color() {
        return color;
    }

    public Commerce commerce() {
        return commerce;
    }

    public com.recordGenerator.javafaker.Company company() {
        return company;
    }

    public com.recordGenerator.javafaker.Crypto crypto() {
        return crypto;
    }

    public com.recordGenerator.javafaker.Hacker hacker() {
        return hacker;
    }

    public IdNumber idNumber() {
        return idNumber;
    }

    public Options options() {
        return options;
    }

    public Code code() {
        return code;
    }

    public File file() {
        return file;
    }

    public Finance finance() {
        return finance;
    }

    public Food food() {
        return food;
    }

    public GameOfThrones gameOfThrones() {
        return gameOfThrones;
    }

    public DateAndTime date() {
        return dateAndTime;
    }

    public Demographic demographic() {
        return demographic;
    }

    public Educator educator() {
        return educator;
    }

    public SlackEmoji slackEmoji() {
        return slackEmoji;
    }

    public Shakespeare shakespeare() {
        return shakespeare;
    }

    public Space space() {
        return space;
    }

    public com.recordGenerator.javafaker.Superhero superhero() {
        return superhero;
    }

    public Bool bool() {
        return bool;
    }

    public Team team() {
        return team;
    }

    public Beer beer() {
        return beer;
    }

    public com.recordGenerator.javafaker.University university() {
        return university;
    }

    public Cat cat() {
        return cat;
    }

    public Stock stock() {
        return stock;
    }

    public String resolve(String key) {
        return this.fakeValuesService.resolve(key, this, this);
    }

    /**
     * Allows the evaluation of native YML expressions to allow you to build your own.
     *
     * The following are valid expressions:
     * <ul>
     *     <li>#{regexify '(a|b){2,3}'}</li>
     *     <li>#{regexify '\\.\\*\\?\\+'}</li>
     *     <li>#{bothify '????','false'}</li>
     *     <li>#{Name.first_name} #{Name.first_name} #{Name.last_name}</li>
     *     <li>#{number.number_between '1','10'}</li>
     * </ul>
     * @param expression (see examples above)
     * @return the evaluated string expression
     * @throws RuntimeException if unable to evaluate the expression
     */
    public String expression(String expression) {
        return this.fakeValuesService.expression(expression, this);
    }
}
