import java.util.ArrayList;
import java.util.Scanner;

//Damian Mały WSTI@2019
abstract class vFS {    //JednostkaWzorcowa
    public vFS(String nazwa, String typ) {
        this.nazwa = nazwa;
        this.typ = typ;
        pHddId = null;
        pDirId = null;
        pFileId = null;
        sLevel = "|__________";
        spLevel = "          ";
        level = 0;
        recursive = false;
    }

    public abstract void info();

    public abstract vFS getZawartosc(int src);

    public abstract void setZawartosc(int src, int dest);

    protected String nazwa; // nazwa
    protected String typ; // typ file, dir, hdd
    protected dysk pHddId; // id dysku
    protected katalog pDirId; // id katalogu
    protected plik pFileId; // id pliku
    protected String sLevel; // wydruk przesuniecie
    protected String spLevel; // wydruk spacje
    protected int level; // poziom zagniezdzenia
    protected boolean recursive; // pokaz zawartosc rekursywnie (tree)

    public void tree() { // pwd
        System.out
                .println(spLevel.substring(0, 2 * level) + sLevel.substring(0, level * 2) + "[" + typ + "] : " + nazwa);
    }

    public void setRecursive() {
        recursive = true;
    }

    public void removeRecursive() {
        recursive = false;
    }

    public void setlevel(int level) {
        this.level = level;
    }

    public int getlevel() {
        return level;
    }

}

class plik extends vFS {    //JednostkaPodstawowa
    public plik(String nazwa, String typ) {
        super(nazwa, typ);
    }

    @Override
    public void info() {
        if (recursive)
            tree();
    }

    @Override
    public vFS getZawartosc(int src) {
        return null;
    }

    @Override
    public void setZawartosc(int src, int dest) {

    }
}

class katalog extends vFS { //JednostkaOrganizacyjna
    public katalog(String nazwa, String typ) {
        super(nazwa, typ);
    }

    public ArrayList<vFS> zawartosc = new ArrayList<vFS>();

    @Override
    public vFS getZawartosc(int src) {
        return zawartosc.get(src);
    }

    @Override
    public void setZawartosc(int src, int dest) {
        zawartosc.set(dest, getZawartosc(src));
    }

    public void add(vFS j) { // touch, mkdir
        if (j.nazwa.equals("."))
            return;
        for (vFS j1 : zawartosc) {
            if (j1.nazwa.equals(j.nazwa)) {
                System.out.println(" -Name '" + j.nazwa + "' exists.");
                return;
            }
        }
        if (zawartosc.size() == 0) // katalog . konfiguracja
        {
            vFS tempPLK = new plik(".", "file");
            zawartosc.add(tempPLK);
            tempPLK.pDirId = this;
            tempPLK.setlevel(getlevel() + 1);
        }
        zawartosc.add(j);
        j.pDirId = this;
        j.setlevel(getlevel() + 1);
    }

    public void del(String nazwa, String typ) {
        if (nazwa.equals("."))
            return;
        for (vFS j : zawartosc) {
            if (j.nazwa.equals(nazwa)) {
                if (j.typ.equals(typ)) {
                    zawartosc.remove(j);
                    return;
                } else {
                    System.out.println(" -Name '" + nazwa + "' is not a '" + typ + "'");
                    return;
                }
            }
        }
        System.out.println(" -Name '" + nazwa + "' not exists.");
    }

    public void move(String nazwa, String nazwa2, boolean kasuj) { // cp, mv
        if (nazwa.equals(".") || nazwa2.equals("."))
            return;
        for (vFS j : zawartosc) {
            if (j.nazwa.equals(nazwa)) { // nazwa exists
                vFS temp = j;
                for (vFS j2 : zawartosc) {
                    if (j2.nazwa.equals(nazwa2)) { // nazwa2 exists
                        zawartosc.remove(j2);
                        break;
                    }
                }
                // nazwa2 not exists
                if (kasuj)
                    zawartosc.remove(j);
                temp.nazwa = nazwa2;
                zawartosc.add(temp);
                if (!kasuj) {
                    System.out.println("cp - duplikuje obiekt (referencje)");
                    System.out.println("W tym miejscu nalezy zaimplementowac");
                    System.out.println("kopiowanie ArrayList za pomocą: DeepCopy");
                }
                return;
            }
        }
        // nazwa not exists
        System.out.println(" -Name '" + nazwa + "' not exists.");
    }

    public void rename(String nazwa, String nazwa2) { // rename
        if (nazwa.equals(".") || nazwa2.equals("."))
            return;
        for (vFS j : zawartosc) {
            if (j.nazwa.equals(nazwa)) { // nazwa exists
                for (vFS j2 : zawartosc) {
                    if (j2.nazwa.equals(nazwa2)) { // nazwa2 exists
                        System.out.println(" -Name '" + nazwa2 + "' exists.");
                        return;
                    }
                }
                // nazwa2 not exists
                j.nazwa = nazwa2;
                return;
            }
        }
        // nazwa not exists
        System.out.println(" -Name '" + nazwa + "' not exists.");
    }

    public vFS change(String nazwa) { // cd
        if (nazwa.equals("."))
            return null;
        for (vFS j : zawartosc) {
            if (nazwa.equals(".."))
                return j;
            if (j.nazwa.equals(nazwa)) { // nazwa exists
                switch (j.typ) {
                case "hdd":
                    return j;
                case "dir":
                    return j;
                case "file":
                    break;
                default:
                    System.out.println("-System failed.");
                }
                return null;
            }
        }
        // nazwa not exists
        System.out.println(" -Directory '" + nazwa + "' not exists.");
        return null;
    }

    @Override
    public void info() { // tree, ls
        tree();
        for (vFS j : zawartosc) {
            if (j.nazwa.equals("."))
                continue;
            if (recursive) // tree
            {
                j.setRecursive();
                j.info();
            } else // ls
                j.tree();
        }
    }

}

class dysk extends vFS {    //JednostkaOrganizacyjna
    public dysk(String nazwa, String typ) {
        super(nazwa, typ);
    }

    private ArrayList<vFS> zawartosc = new ArrayList<vFS>();

    @Override
    public vFS getZawartosc(int src) {
        return zawartosc.get(src);
    }

    @Override
    public void setZawartosc(int src, int dest) {
        zawartosc.set(dest, getZawartosc(src));
    }

    public void add(vFS j) { // touch, mkdir
        if (j.nazwa.equals("."))
            return;
        for (vFS j1 : zawartosc) {
            if (j1.nazwa.equals(j.nazwa)) {
                System.out.println(" -Name '" + j.nazwa + "' exists.");
                return;
            }
        }
        if (zawartosc.size() == 0) // katalog . konfiguracja
        {
            vFS tempPLK = new plik(".", "file");
            zawartosc.add(tempPLK);
            tempPLK.pHddId = this;
            tempPLK.setlevel(getlevel() + 1);
        }
        zawartosc.add(j);
        j.pHddId = this;
        j.setlevel(getlevel() + 1);
    }

    public void del(String nazwa, String typ) { // rm, rmdir
        if (nazwa.equals("."))
            return;
        for (vFS j : zawartosc) {
            if (j.nazwa.equals(nazwa)) {
                if (j.typ.equals(typ)) {
                    zawartosc.remove(j);
                    return;
                } else {
                    System.out.println(" -Name '" + nazwa + "' is not a '" + typ + "'");
                    return;
                }

            }
        }
        System.out.println(" -Name '" + nazwa + "' not exists.");
    }

    public void move(String nazwa, String nazwa2, boolean kasuj) { // cp, mv
        if (nazwa.equals(".") || nazwa2.equals("."))
            return;
        for (vFS j : zawartosc) {
            if (j.nazwa.equals(nazwa)) { // nazwa exists
                vFS temp = j;
                for (vFS j2 : zawartosc) {
                    if (j2.nazwa.equals(nazwa2)) { // nazwa2 exists
                        zawartosc.remove(j2);
                        break;
                    }
                }
                // nazwa2 not exists
                if (kasuj)
                    zawartosc.remove(j);
                temp.nazwa = nazwa2;
                zawartosc.add(temp);
                if (!kasuj) {
                    System.out.println("cp - duplikuje obiekt (referencje)");
                    System.out.println("W tym miejscu nalezy zaimplementowac");
                    System.out.println("kopiowanie ArrayList za pomocą: DeepCopy");
                }
                return;
            }
        }
        // nazwa not exists
        System.out.println(" -Name '" + nazwa + "' not exists.");
    }

    public void rename(String nazwa, String nazwa2) { // rename
        if (nazwa.equals(".") || nazwa2.equals("."))
            return;
        for (vFS j : zawartosc) {
            if (j.nazwa.equals(nazwa)) { // nazwa exists
                for (vFS j2 : zawartosc) {
                    if (j2.nazwa.equals(nazwa2)) { // nazwa2 exists
                        System.out.println(" -Name '" + nazwa2 + "' exists.");
                        return;
                    }
                }
                // nazwa2 not exists
                j.nazwa = nazwa2;
                return;
            }
        }
        // nazwa not exists
        System.out.println(" -Name '" + nazwa + "' not exists.");
    }

    public vFS change(String nazwa) { // cd
        if (nazwa.equals("."))
            return null;
        for (vFS j : zawartosc) {
            if (nazwa.equals(".."))
                return j;
            if (j.nazwa.equals(nazwa)) { // nazwa exists
                switch (j.typ) {
                case "hdd":
                    return j;
                case "dir":
                    return j;
                case "file":
                    break;
                default:
                    System.out.println("-System failed.");
                }
                return null;
            }
        }
        // nazwa not exists
        System.out.println(" -Directory '" + nazwa + "' not exists.");
        return null;
    }

    @Override
    public void info() { // tree, ls
        tree();
        for (vFS j : zawartosc) {
            if (j.nazwa.equals("."))
                continue;
            if (recursive) // tree
            {
                j.setRecursive();
                j.info();
            } else // ls
                j.tree();
        }
    }
}

// System
class Psystem {

    public Psystem(dysk hdd, katalog dir, plik file) {
        this.hdd = hdd;
        this.dir = dir;
        this.file = file;
    }

    private dysk hdd;
    private katalog dir;
    private plik file;

    public void setHdd(dysk hdd) {
        this.hdd = hdd;
    }

    public void setDir(katalog dir) {
        this.dir = dir;
    }

    public void setFile(plik file) {
        this.file = file;
    }

    private String checkType() {
        if (hdd != null)
            return hdd.typ;
        if (dir != null)
            return dir.typ;
        if (file != null)
            return file.typ;
        return "null";
    }

    public void cd(String arg) {
        vFS temp = null;
        switch (checkType()) {
        case "hdd":
            if (arg.equals("..")) {
                if (hdd.pHddId != null)
                    temp = hdd.pHddId;
                else if (hdd.pDirId != null)
                    temp = hdd.pDirId;
                else
                    temp = hdd.pFileId;
            } else
                temp = hdd.change(arg);
            break;
        case "dir":
            if (arg.equals("..")) {
                if (dir.pHddId != null)
                    temp = dir.pHddId;
                else if (dir.pDirId != null)
                    temp = dir.pDirId;
                else
                    temp = dir.pFileId;
            } else
                temp = dir.change(arg);
            break;
        case "file":
            break;
        default:
            System.out.println("-System failed.");
        }
        if (temp != null) {
            setHdd(temp.getZawartosc(0).pHddId);
            setDir(temp.getZawartosc(0).pDirId);
            setFile(temp.getZawartosc(0).pFileId);
        }

    }

    public void rename(String arg, String arg2) {
        if (arg != "" && arg2 != "") {
            if (arg.equals(arg2)) {
                System.out.println("-This are the same names.");
                return;
            }
            switch (checkType()) {
            case "hdd":
                hdd.rename(arg, arg2);
                break;
            case "dir":
                dir.rename(arg, arg2);
                break;
            case "file":
                break;
            default:
                System.out.println("-System failed.");
            }
        } else {
            System.out.println(" -Arguments not set.");
        }
    }

    public void cp(String arg, String arg2) {
        if (arg != "" && arg2 != "") {
            if (arg.equals(arg2)) {
                System.out.println("-This are the same names.");
                return;
            }
            switch (checkType()) {
            case "hdd":
                hdd.move(arg, arg2, false);
                break;
            case "dir":
                dir.move(arg, arg2, false);
                break;
            case "file":
                break;
            default:
                System.out.println("-System failed.");
            }
        } else {
            System.out.println(" -Arguments not set.");
        }
    }

    public void mv(String arg, String arg2) {
        if (arg != "" && arg2 != "") {
            if (arg.equals(arg2)) {
                System.out.println("-This are the same names.");
                return;
            }
            switch (checkType()) {
            case "hdd":
                hdd.move(arg, arg2, true);
                break;
            case "dir":
                dir.move(arg, arg2, true);
                break;
            case "file":
                break;
            default:
                System.out.println("-System failed.");
            }
        } else {
            System.out.println(" -Arguments not set.");
        }
    }

    public void rmdir(String arg) {
        if (arg != "") {
            switch (checkType()) {
            case "hdd":
                hdd.del(arg, "dir");
                break;
            case "dir":
                dir.del(arg, "dir");
                break;
            case "file":
                break;
            default:
                System.out.println("-System failed.");
            }
        } else {
            System.out.println(" -Dirname argument not set.");
        }
    }

    public void rm(String arg) {
        if (arg != "") {
            switch (checkType()) {
            case "hdd":
                hdd.del(arg, "file");
                break;
            case "dir":
                dir.del(arg, "file");
                break;
            case "file":
                break;
            default:
                System.out.println("-System failed.");
            }
        } else {
            System.out.println(" -Filename argument not set.");
        }
    }

    public void touch(String arg) {
        if (arg != "") {
            switch (checkType()) {
            case "hdd":
                hdd.add(new plik(arg, "file"));
                break;
            case "dir":
                dir.add(new plik(arg, "file"));
                break;
            case "file":
                break;
            default:
                System.out.println("-System failed.");
            }
        } else {
            System.out.println(" -Filename argument not set.");
        }
    }

    public void mkdir(String arg) {
        if (arg != "") {
            switch (checkType()) {
            case "hdd":
                hdd.add(new katalog(arg, "dir"));
                break;
            case "dir":
                dir.add(new katalog(arg, "dir"));
                break;
            case "file":
                break;
            default:
                System.out.println("-System failed.");
            }
        } else {
            System.out.println(" -Dirname argument not set.");
        }
    }

    public void ls() {
        switch (checkType()) {
        case "hdd":
            hdd.removeRecursive();
            hdd.info();
            break;
        case "dir":
            dir.removeRecursive();
            dir.info();
            break;
        case "file":
            break;
        default:
            System.out.println("-System failed.");
        }
    }

    public void tree() {
        switch (checkType()) {
        case "hdd":
            hdd.setRecursive();
            hdd.info();
            break;
        case "dir":
            dir.setRecursive();
            dir.info();
            break;
        case "file":
            break;
        default:
            System.out.println("-System failed.");
        }
    }

    public void pwd() {
        switch (checkType()) {
        case "hdd":
            hdd.tree();
            break;
        case "dir":
            dir.tree();
            break;
        case "file":
            break;
        default:
            System.out.println("-System failed.");
        }
    }

    public void quit() {
        System.out.println("Linux shutdown.");
    }

    public void help() {
        String polecenia = "- Usage: [q]uit [h]elp [p]wd [l]s [t]ree [to]uch [mk]dir rm [rd]rmdir cp mv [rn]rename cd [cd ..]";
        System.out.println(polecenia);
    }

    public void err(String command) {
        System.out.println(" -'" + command + "' command not found.");
    }

    public void none() {
    }

}

// MAIN

public class virtualFS {

    public static void main(String[] args) throws Exception {

        System.out.println("virtualFS - kompozytor (Damian Mały)");

        // Initial structure
        dysk HDD;
        katalog KAT;

        HDD = new dysk("sda2", "hdd");
        HDD.add(KAT = new katalog("root", "dir"));
        KAT.add(new plik("tajne.txt", "file"));
        KAT.add(new plik("sprawko.txt", "file"));

        HDD.add(KAT = new katalog("home", "dir"));
        KAT.add(new plik("user.txt", "file"));
        KAT.add(new plik("password.txt", "file"));
        KAT.add(KAT = new katalog("uzytkownicy", "dir"));
        KAT.add(new plik("jacek.txt", "file"));
        KAT.add(new plik("placek.txt", "file"));

        HDD.add(KAT = new katalog("usr", "dir"));
        KAT.add(KAT = new katalog("bin", "dir"));
        KAT.add(new plik("ls", "file"));
        KAT.add(new plik("dir", "file"));

        // Linux main
        System.out.println("Virtual Linux v1.0");

        Psystem actual = new Psystem(HDD, null, null);
        String line, command, arg, arg2;
        actual.help();
        Scanner sc = new Scanner(System.in);
        // Consola
        loop: while (true) {
            System.out.print("> ");
            line = sc.nextLine();
            String[] split = line.split(" ");
            command = "";
            arg = "";
            arg2 = "";
            if (split.length > 0)
                command = split[0];
            if (split.length > 1)
                arg = split[1];
            if (split.length > 2)
                arg2 = split[2];

            switch (command) {
            case "q":
            case "quit":
                actual.quit();
                break loop;
            case "cd":
                actual.cd(arg);
                break;
            case "rn":
            case "rename":
                actual.rename(arg, arg2);
                break;
            case "mv":
                actual.mv(arg, arg2);
                break;
            case "cp":
                actual.cp(arg, arg2);
                break;
            case "rm":
                actual.rm(arg);
                break;
            case "rd":
            case "rmdir":
                actual.rmdir(arg);
                break;
            case "to":
            case "touch":
                actual.touch(arg);
                break;
            case "mk":
            case "mkdir":
                actual.mkdir(arg);
                break;
            case "t":
            case "tree":
                actual.tree();
                break;
            case "l":
            case "ls":
                actual.ls();
                break;
            case "p":
            case "pwd":
                actual.pwd();
                break;
            case "h":
            case "help":
                actual.help();
                break;
            case "":
                actual.none();
                break;
            default:
                actual.err(command);
                break;
            }

        }
        sc.close();
    }
}