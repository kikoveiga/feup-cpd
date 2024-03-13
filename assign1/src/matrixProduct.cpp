#include <iostream>
#include <stdio.h>
#include <iomanip>
#include <time.h>
#include <cstdlib>
#include <papi.h>
#include <omp.h>
#include <algorithm>

using namespace std;

#define SYSTEMTIME clock_t

 
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

	// display 10 elements of the result matrix tto verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++) {
        for(j=0; j<min(10,m_br); j++) cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
	
	
}

// add code here for line x line matrix multiplication
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

    // display 10 elements of the result matrix tto verify correctness
    cout << "Result matrix: " << endl;
    for(i=0; i<1; i++) {
		for(j=0; j<min(10,m_br); j++)
            cout << phc[j] << " ";
    }
    cout << endl;
    
    free(pha);
    free(phb);
    free(phc);
}

// add code here for block x block matrix multiplication
void OnMultBlock(int m_ar, int m_br, int bkSize) {
    SYSTEMTIME Time1, Time2;
    double *pha, *phb, *phc;
    int i, j, k, i0, j0, k0;

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

    // Initialize result matrix
    for (i = 0; i < m_ar; i++)
        for (j = 0; j < m_br; j++)
            phc[i*m_ar + j] = 0.0;

    // Block-by-block multiplication
    for (i0 = 0; i0 < m_ar; i0 += bkSize) {
        for (k0 = 0; k0 < m_ar; k0 += bkSize) {
            for (j0 = 0; j0 < m_ar; j0 += bkSize) {
                // Ensure the blocks fit within the cache sizes
                for (i = i0; i < min(i0 + bkSize, m_ar); i++) {
                    for (k = k0; k < min(k0 + bkSize, m_ar); k++) {
                        double temp = pha[i*m_ar + k]; // Prefetch this value to reduce memory accesses
                        for (j = j0; j < min(j0 + bkSize, m_br); j++) {
                            // Access phc and phb in a more cache-friendly pattern
                            phc[i*m_ar + j] += temp * phb[k*m_br + j];
                        }
                    }
                }
            }
        }
    }

    Time2 = clock();

    // Print execution time
    cout << "Time: " << fixed << setprecision(3) << (double)(Time2 - Time1) / CLOCKS_PER_SEC << " seconds\n";

    // display 10 elements of the result matrix tto verify correctness
    cout << "Result matrix: " << endl;
    for(i=0; i<1; i++) {
        for(j=0; j<min(10,m_br); j++) cout << phc[j] << " ";
    }
    cout << endl;
    
    free(pha);
    free(phb);
    free(phc);
}

void OnMultLineParallel1(int m_ar, int m_br) {
    double start_time, end_time; // Use doubles to hold the start and end times
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

    start_time = omp_get_wtime(); // Capture start time

    // Line-by-line multiplication
    #pragma omp parallel for private (k, j)
    for (i = 0; i < m_ar; i++) {
        for (k = 0; k < m_ar; k++) {
            double temp = pha[i * m_ar + k];
            for (j = 0; j < m_br; j++) {
                phc[i * m_ar + j] += temp * phb[k * m_br + j];
            }
        }
    }

    end_time = omp_get_wtime(); // Capture end time

    // Print execution time
    cout << "Time: " << fixed << setprecision(3) << (end_time - start_time) << " seconds\n";

    // Display 10 elements of the result matrix to verify correctness
    cout << "Result matrix: " << endl;
    for(i = 0; i < 1; i++) {
        for(j = 0; j < min(10, m_br); j++)
            cout << phc[j] << " ";
    }
    cout << endl;
    
    free(pha);
    free(phb);
    free(phc);
}

void OnMultLineParallel2(int m_ar, int m_br) {
    double start_time, end_time; // Use doubles to hold the start and end times
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

    start_time = omp_get_wtime(); // Capture start time

    #pragma omp parallel private (i,k)
    for (int i = 0; i < m_ar; i++) {
        for (int k = 0; k < m_ar; k++) {
            double temp = pha[i * m_ar + k];
            #pragma omp for
            for (int j = 0; j < m_br; j++) {
                phc[i * m_ar + j] += temp * phb[k * m_br + j];
            }
        }
    }

    end_time = omp_get_wtime(); // Capture end time

    // Print execution time
    cout << "Time: " << fixed << setprecision(3) << (end_time - start_time) << " seconds\n";

    // Display 10 elements of the result matrix to verify correctness
    cout << "Result matrix: " << endl;
    for(i = 0; i < 1; i++) {
        for(j = 0; j < min(10, m_br); j++)
            cout << phc[j] << " ";
    }
    cout << endl;
    
    free(pha);
    free(phb);
    free(phc);
}

void handle_error (int retval)
{
  printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(1);
}

void init_papi() {
  int retval = PAPI_library_init(PAPI_VER_CURRENT);
  if (retval != PAPI_VER_CURRENT && retval < 0) {
    printf("PAPI library version mismatch!\n");
    exit(1);
  }
  if (retval < 0) handle_error(retval);

  std::cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
            << " MINOR: " << PAPI_VERSION_MINOR(retval)
            << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
}


int main (int argc, char *argv[])
{

	char c;
	int lin, col, blockSize;
	int op;
	
	int EventSet = PAPI_NULL;
  	long long values[2];
  	int ret;
	

	ret = PAPI_library_init( PAPI_VER_CURRENT );
	if ( ret != PAPI_VER_CURRENT )
		std::cout << "FAIL" << endl;


	ret = PAPI_create_eventset(&EventSet);
		if (ret != PAPI_OK) cout << "ERROR: create eventset" << endl;


	ret = PAPI_add_event(EventSet,PAPI_L1_DCM );
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L1_DCM" << endl;


	ret = PAPI_add_event(EventSet,PAPI_L2_DCM);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCM" << endl;


	op=1;
	do {
		cout << endl << "1. Multiplication" << endl;
		cout << "2. Line Multiplication" << endl;
		cout << "3. Block Multiplication" << endl;
        cout << "4. Line Multiplication (multi-core v1)" << endl;
        cout << "5. Line Multiplication (multi-core v2)" << endl;
		cout << "Selection?: ";
		cin >>op;
		if (op == 0)
			break;
		printf("Dimensions: lins=cols ? ");
   		cin >> lin;
   		col = lin;


		// Start counting
		ret = PAPI_start(EventSet);
		if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

		switch (op){
			case 1: 
				OnMult(lin, col);
				break;
			case 2:
				OnMultLine(lin, col);  
				break;
			case 3:
				cout << "Block Size? ";
				cin >> blockSize;
				OnMultBlock(lin, col, blockSize);  
				break;
            case 4:
                OnMultLineParallel1(lin, col);  
				break;
            case 5:
                OnMultLineParallel2(lin, col);
                break;
		}

  		ret = PAPI_stop(EventSet, values);
  		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;
  		printf("L1 DCM: %lld \n",values[0]);
  		printf("L2 DCM: %lld \n",values[1]);

		ret = PAPI_reset( EventSet );
		if ( ret != PAPI_OK )
			std::cout << "FAIL reset" << endl; 

	}while (op != 0);

	ret = PAPI_remove_event( EventSet, PAPI_L1_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L2_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_destroy_eventset( &EventSet );
	if ( ret != PAPI_OK )
		std::cout << "FAIL destroy" << endl;

}