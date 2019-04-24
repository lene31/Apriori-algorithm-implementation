package dm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Apriori {

	public static void main(String[] args) throws Exception {

		System.out.println("Enter the minimum support : ");
		Scanner in = new Scanner(System.in);
		float min_supp = in.nextFloat();
		min_supp *= 10;
		int min_support = (int) min_supp;

		System.out.println("Enter the minimum confidence : ");
		float min_con = in.nextFloat();
		min_con *= 100;
		int min_conf = (int) min_con;

		in.close();

		String line_f;
		int size_f = 0;
		Set<String> set_f = new HashSet<>();

		System.out.println("\n\nMinimum Support = " + min_support*10 + "%\nMinimum Confidence = " + min_conf+"%");
		
		for (int data_loop = 1; data_loop < 6; data_loop++) {

			

			String data_sel_f = "src\\dm\\data_" + data_loop + ".txt";

			BufferedReader buffer_f = new BufferedReader(new FileReader(data_sel_f));

			while ((line_f = buffer_f.readLine()) != null) {
				String[] vals = line_f.trim().split("\\s+");
				size_f = vals.length;
				for (int col = 0; col < size_f; col++) {
					set_f.add(vals[col]);
					set_f.remove(null);

				}
			}
			buffer_f.close();
		}

		System.out.println("\nAll items used : " + set_f);

		for (int data_loop = 1; data_loop < 6; data_loop++) {

			String data_sel = "src\\dm\\data_" + data_loop + ".txt";
			System.out.println("\n\nAssociation rules for database : " + data_loop + "\n");

			int[][] matrix = new int[20][20];

			BufferedReader buffer = new BufferedReader(new FileReader(data_sel));

			String line;
			int row = 0;
			int size = 0;
			int[] support_1 = new int[10];

			while ((line = buffer.readLine()) != null) {
				String[] vals = line.trim().split("\\s+");
				size = vals.length;
				for (int col = 0; col < size; col++) {

					switch (vals[col]) {
					case "towel":
						vals[col] = "1";
						support_1[1]++;
						break;
					case "pen":
						vals[col] = "2";
						support_1[2]++;
						break;
					case "mug":
						vals[col] = "3";
						support_1[3]++;
						break;
					case "milk":
						vals[col] = "4";
						support_1[4]++;
						break;
					case "coffee":
						vals[col] = "5";
						support_1[5]++;
						break;
					case "detergent":
						vals[col] = "6";
						support_1[6]++;
						break;
					case "foil":
						vals[col] = "7";
						support_1[7]++;
						break;
					case "battery":
						vals[col] = "8";
						support_1[8]++;
						break;
					case "bulb":
						vals[col] = "9";
						support_1[9]++;
						break;
					case "sponge":
						vals[col] = "10";
						support_1[0]++;
						break;
					}

					matrix[row][col] = Integer.parseInt(vals[col]);

				}

				row++;
			}

			buffer.close();

			// -------Stage 1--------------------------------------------------

			int[] support_1_post = new int[10];
			int len = 0;
			for (int i = 0; i < 10; i++) {
				if (support_1[i] >= min_support) {
					support_1_post[len] = i;
					len++;
				}

			}
			int support_1_pre[] = Arrays.copyOf(support_1_post, len);

			// -------Stage 2--------------------------------------------------

			int m_len = (len * (len - 1)) / 2;

			// Creating pair
			int[][] support_2 = new int[m_len][3];
			int k = 0, jj = 0;
			for (int i = 0; i < len; i++) {
				for (jj = jj + i + 1; jj < len; jj++) {
					support_2[k][0] = support_1_pre[i];
					support_2[k][1] = support_1_pre[jj];
					k++;
				}
				jj = 0;
			}

			for (int mm = 0; mm < m_len; mm++) {
				for (int m = 0; m < 20; m++) {
					for (int i = 0; i < 20; i++) {

						if (support_2[mm][0] == matrix[m][i]) {

							for (int j = 0; j < 20; j++) {

								if (support_2[mm][1] == matrix[m][j]) {
									support_2[mm][2]++;
								}
							}
						}
					}
				}
			}

			int[][] support_2_post = new int[m_len][3];
			len = 0;
			for (int i = 0; i < m_len; i++) {
				if (support_2[i][2] >= min_support) {
					support_2_post[len][0] = support_2[i][0];
					support_2_post[len][1] = support_2[i][1];
					support_2_post[len][2] = support_2[i][2];
					len++;
				}

			}
			int support_pre[][] = Arrays.copyOf(support_2_post, len);

			do {
				Printing(support_pre, matrix, min_support, min_conf);

				int[][] pairing_array = Pairing(support_pre);

				if (pairing_array.length != 0) {
					support_pre = Duplicate(pairing_array, matrix, min_support);
				} else {
					break;
				}

				support_pre = Duplicate(pairing_array, matrix, min_support);
			} while (support_pre.length != 0);

		}
	}

	private static void Printing(int[][] duplicate_array, int[][] matrix, int min_support, int min_conf) {

		Integer[][] printing = new Integer[duplicate_array.length][duplicate_array[0].length - 1];
		for (int i = 0; i < duplicate_array.length; i++) {

			for (int j = 0; j < duplicate_array[0].length - 1; j++) {

				printing[i][j] = duplicate_array[i][j];
			}

		}

		int n = printing[0].length;

		int n_inner = (int) Math.pow(2, n);

		for (int k = 0; k < printing.length; k++) {

			int m = 0;
			int[][] supp_cal = new int[n_inner][3];

			for (int i = 0; i < (1 << n); i++) {

				for (int j = 0; j < n; j++) {

					if ((i & (1 << j)) > 0) {

						supp_cal[m][0] = supp_cal[m][0] * 10 + printing[k][j];
						supp_cal[n_inner - m - 1][1] = supp_cal[n_inner - m - 1][1] * 10 + printing[k][j];

					}

				}

				m++;

			}

			int[][] supp_cal_post = support_cal(supp_cal, matrix);

			DecimalFormat decimalFormat = new DecimalFormat("#.##");

			String[][] supp_cal_post_main = new String[supp_cal_post.length][supp_cal_post[0].length];

			for (int i1 = 0; i1 < supp_cal_post.length; i1++) {
				for (int i11 = 0; i11 < supp_cal_post[0].length; i11++) {
					supp_cal_post_main[i1][i11] = Integer.toString(supp_cal_post[i1][i11]);
					// String str1 = Integer.toString(a);
				}
			}

			for (int i1 = 0; i1 < supp_cal_post_main.length; i1++) {
				for (int i11 = 0; i11 < supp_cal_post_main[0].length; i11++) {

					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("1", "towel ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("2", "pen ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("3", "mug ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("4", "milk ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("5", "coffee ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("6", "detergent ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("7", "foil ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("8", "battery ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("9", "bulb ");
					supp_cal_post_main[i1][i11] = supp_cal_post_main[i1][i11].replace("0", "sponge ");
				}
			}

			// Final printing
			for (int i1 = 0; i1 < supp_cal_post_main.length; i1++) {

				if (supp_cal_post[i1][supp_cal_post[0].length - 1] >= min_support
						&& supp_cal_post[supp_cal_post.length - 1][2] >= min_support) {

					float supp_prep = supp_cal_post[supp_cal_post.length - 1][2];
					float conf_prep = supp_cal_post[i1][1];
					float conf = (supp_prep / conf_prep) * 100;
					float supp = (supp_prep / 10) * 100;

					if (conf >= min_conf && conf < 101) {

						System.out.println(supp_cal_post_main[i1][0] + " ==> " + supp_cal_post_main[i1][1] + "["
								+ Float.valueOf(decimalFormat.format(supp)) + "%, "
								+ Float.valueOf(decimalFormat.format(conf)) + "%]");

					}

				}

			}

		}

	}

	private static int[][] support_cal(int[][] supp_cal, int[][] matrix) {

		// calculating support
		for (int i = 0; i < supp_cal.length; i++) {

			int sup_val = supp_cal[i][0];
			int sup_val_test = 0;
			int cnt = 0;
			int cnt_f = 0;

			for (int i1 = 0; i1 < 20; i1++) {
				sup_val = supp_cal[i][0];
				cnt = 0;
				cnt_f = 0;

				do {

					sup_val_test = sup_val % 10;

					for (int j = 0; j < 20; j++) {

						if (sup_val_test == matrix[i1][j] && sup_val_test != 0) {

							cnt++;
						}

					}

					sup_val /= 10;

					cnt_f++;
				} while (sup_val > 0);

				if (cnt == cnt_f) {
					supp_cal[i][supp_cal[0].length - 1]++;
				}

			}

		}
		return supp_cal;

	}

	private static int[][] Pairing(int pair[][]) {

		// Pairing

		int[][] pairing_array = pair;

		int no_array = pairing_array[0].length;

		for (int i = 0; i < pairing_array.length; i++) {
			pairing_array[i][no_array - 1] = 0;
		}

		int c_pair = no_array - 1;
		int c_pair_main = pairing_array.length;
		int comb_pair = (c_pair_main * (c_pair_main - 1)) / 2;
		int comb_len = c_pair * 2;

		int[][] support_pair = new int[comb_pair][comb_len];

		int k = 0;
		int jj = 0;
		for (k = 0; k < support_pair.length; k++) {
			for (int i = 0; i < pairing_array.length; i++) {
				for (jj = jj + i + 1; jj < pairing_array.length; jj++) {
					for (int j = 0; j < no_array - 1; j++) {

						support_pair[k][j] = pairing_array[i][j];
						support_pair[k][j + c_pair] = pairing_array[jj][j];

					}
					k++;
				}
				jj = 0;
			}
		}

		return support_pair;

	}

	private static int[][] Duplicate(int pair[][], int matrix[][], int min_support) {

		int[][] support_3 = pair;

		int len = support_3.length;

		int len_in = support_3[0].length;
		int len_cal = (len_in / 2) + 2;

		String set_s = "";
		Set<String> set3 = new HashSet<String>();
		Set<Integer> ints = new HashSet<Integer>();
		for (int j = 0; j < len; j++) {

			Arrays.sort(support_3[j]);

			Integer array_sort[] = new Integer[len_in];
			for (int i = 0; i < len_in; i++) {
				array_sort[i] = support_3[j][i];
			}

			Set<Integer> set = new HashSet<>(Arrays.asList(array_sort));
			set.remove(null);

			Set<Integer> set2 = new HashSet<Integer>();

			if (set.toArray().length == len_cal - 1) {
				set2 = new HashSet<>(set);
			}

			set_s = "";
			if (!set2.isEmpty()) {
				for (int i = 0; i < set2.size(); i++) {
					set_s = set_s + (String) set2.toArray()[i].toString();

				}
				set3.add(set_s);
				ints = set3.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toSet());
			}

		}

		Integer[] array = ints.stream().toArray(Integer[]::new);
		Arrays.sort(array);

		int[][] support_3_post = new int[array.length][len_cal];

		for (int i = 0; i < array.length; i++) {
			support_3_post[i][0] = array[i];
		}

		int val_digit = (int) (Math.log10(support_3_post[0][0]) + 1);

		// -----------------------------------------------------------------------------------------

		// calculating support
		for (int i = 0; i < array.length; i++) {

			int sup_val = support_3_post[i][0];
			int sup_val_test = 0;
			int cnt = 0;
			int cnt_f = 0;

			for (int i1 = 0; i1 < 20; i1++) {
				sup_val = support_3_post[i][0];
				cnt = 0;
				cnt_f = 0;

				do {

					sup_val_test = sup_val % 10;

					for (int j = 0; j < 20; j++) {

						if (sup_val_test == matrix[i1][j] && sup_val_test != 0) {

							cnt++;
						}

					}

					sup_val /= 10;

					cnt_f++;
				} while (sup_val > 0);

				if (cnt == cnt_f) {
					support_3_post[i][val_digit]++;
				}

				if (i1 == 9) {
					sup_val = support_3_post[i][0];
					for (int i11 = cnt_f; i11 > 0; i11--) {

						sup_val_test = sup_val % 10;
						support_3_post[i][i11 - 1] = sup_val_test;
						sup_val /= 10;

					}

				}

			}

		}

		int[][] support_after_min = new int[array.length][len_in];

		int k = 0;

		for (int i = 0; i < support_3_post.length; i++) {

			if (support_3_post[i][support_3_post[0].length - 1] >= min_support) {

				for (int j = 0; j < support_3_post[0].length; j++) {
					support_after_min[k][j] = support_3_post[i][j];

				}
				k++;
			}

		}

		int[][] support_after_min_f = new int[k][len_cal];

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < support_after_min_f[0].length; j++) {
				support_after_min_f[i][j] = support_after_min[i][j];
			}
		}

		return support_after_min_f;

	}

}
