# CPD1

For the programming languages we chose **C++** and **Python**.

We ran each test 3 times and the results show the average run time.

## Part 1

### 1 - Standard Matrix Multiplication

This algorithm follows the conventional approach of iterating through each row of the first matrix and each column of the second matrix to compute the dot products for the elements of the result matrix. Memory allocation is done linearly for matrices pha, phb, and phc, with initialization ensuring pha is filled with 1.0s and phb with incrementing values starting from 1.0. The multiplication process is straightforward but can be computationally intensive due to the triple nested loop structure, which has a time complexity of O(n^3).

**C++ code**

```cpp
void OnMult(int m_ar, int m_br) {

	SYSTEMTIME Time1, Time2;

	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;



    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;



	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);



    Time1 = clock();

	for(i=0; i<m_ar; i++) {
        for( j=0; j<m_br; j++) {
            temp = 0;
			for( k=0; k<m_ar; k++) {
				temp += pha[i*m_ar+k] * phb[k*m_br+j];
			}
			phc[i*m_ar+j]=temp;
		}
	}


    Time2 = clock();
	sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1) / CLOCKS_PER_SEC);
	cout << st;

    free(pha);
    free(phb);
    free(phc);
}
```

**Python code**

```py
def on_mult(m_ar, m_br):
    pha = np.ones((m_ar, m_ar))
    phb = np.array([[(i+1) for j in range(m_br)] for i in range(m_br)])
    phc = np.zeros((m_ar, m_br))

    start_time = time.time()

    for i in range(m_ar):
        for j in range(m_br):
            phc[i, j] = np.sum(pha[i, :] * phb[:, j])

    end_time = time.time()

    elapsed_time = end_time - start_time
    print("Time: {:.3f} seconds".format(elapsed_time))
```

**Results**

| Matrix Size | C++ Average Time (s) | Python Average Time (s) |
| ----------- | -------------------- | ----------------------- |
| 600         | 0.218                | 2.084                   |
| 1000        | 2.058                | 10.540                  |
| 1400        | 4.947                | 29.213                  |
| 1800        | 27.859               | 69.233                  |
| 2200        | 60.095               | 126.165                 |
| 2600        | 104.743              | 204.968                 |
| 3000        | 171.733              | 300.595                 |

### 2 - Standard Matrix Multiplication

This method optimizes the standard approach by reordering the loops to access memory in a more cache-friendly manner. It initializes the matrices in the same way but changes the multiplication logic to iterate line by line. This allows for better utilization of cache lines, potentially reducing cache misses and improving overall execution time. The algorithm still follows an O(n^3) time complexity but aims to enhance practical performance through improved memory access patterns.

**C++ code**

```cpp
void OnMultLine(int m_ar, int m_br) {
    SYSTEMTIME Time1, Time2;
    double temp;
    int i, j, k;

    double *pha, *phb, *phc;

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    // Initialize matrices
    for (i = 0; i < m_ar; i++)
        for (j = 0; j < m_ar; j++)
            pha[i*m_ar + j] = (double)1.0;

    for (i = 0; i < m_br; i++)
        for (j = 0; j < m_br; j++)
            phb[i*m_br + j] = (double)(i+1);

    Time1 = clock();

    // Line-by-line multiplication
    for (i = 0; i < m_ar; i++) {
        for (k = 0; k < m_ar; k++) {
            temp = pha[i*m_ar+k];
            for (j = 0; j < m_br; j++) {
                phc[i*m_ar+j] += temp * phb[k*m_br+j];
            }
        }
    }

    Time2 = clock();

    // Print execution time
    cout << "Time: " << fixed << setprecision(3) << (double)(Time2 - Time1) / CLOCKS_PER_SEC << " seconds\n";

    free(pha);
    free(phb);
    free(phc);
}
```

**Python code**

```py
def on_mult_line(m_ar, m_br):
    pha = np.ones((m_ar, m_ar))
    phb = np.array([[(i+1) for j in range(m_br)] for i in range(m_br)])
    phc = np.zeros((m_ar, m_br))

    start_time = time.time()

    for i in range(m_ar):
        for j in range(m_br):
            phc[i, j] = np.dot(pha[i, :], phb[:, j])

    end_time = time.time()

    elapsed_time = end_time - start_time
    print("Time: {:.3f} seconds".format(elapsed_time))
```

**Results**

| Size  | C++ Average Time (s) | Python Average Time (s) |
| ----- | -------------------- | ----------------------- |
| 600   | 0.113                | 0.733                   |
| 1000  | 0.620                | 5.192                   |
| 1400  | 1.767                | 14.093                  |
| 1800  | 3.684                | 46.256                  |
| 2200  | 6.843                | 88.408                  |
| 2600  | 11.292               | 149.043                 |
| 3000  | 17.408               | 236.386                 |
| 4096  | 44.353               | N/A                     |
| 6144  | 149.911              | N/A                     |
| 8192  | 355.475              | N/A                     |
| 10240 | 730.939              | N/A                     |
