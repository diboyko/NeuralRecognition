package utility;

/**
 * Created by Me on 20.05.2016.
 */
public enum Letters {

    A(1), B(2), C(3), D(4), E(5), F(6), G(7), H(8), I(9),J(10),K(11),L(12),M(13),N(14),O(15),P(16),Q (17),R(18),S(19),
    T(20),U(21),V(22),W(23),X(24),Y(25),Z(26);

    int position;

    public static String[] Alphabet = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    Letters(int position)
    {
        this.position = position;
    }

    public static double[] convertLetterToArray(Character letter)
    {
        double[] letterAsArray = new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int position = Letters.valueOf(letter.toString().toUpperCase()).position-1;
        letterAsArray[position] = 1;
        return letterAsArray;
    }

    public static Letters convertArrayToLetter(double[] arrayToConvet)
    {
        int positionOfMaxElement = 0;
        double valueOfMaxElement = 0;
        for (int i=0; i<arrayToConvet.length; i++)
        {
            if (arrayToConvet[i] > valueOfMaxElement)
            {
                valueOfMaxElement = arrayToConvet[i];
                positionOfMaxElement = i;
            }
        }
        return Letters.values()[positionOfMaxElement];
    }

    public static String getPathOfLetterExample(Letters letter)
    {

        StringBuilder path = new StringBuilder("letters/");
        path.append(letter.toString());
        path.append(".png");

        return path.toString();
    }

}
