import numpy as np
import time

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

    # Display 10 elements of the result matrix to verify correctness
    """ print("Result matrix:")
    for i in range(1):
        print(phc[i, :min(10, m_br)]) """


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

    # Display 10 elements of the result matrix to verify correctness
    """ print("Result matrix:")
    for i in range(1):
        print(phc[i, :min(10, m_br)])"""

def on_mult_block(m_ar, m_br, bk_size):
    pha = np.ones((m_ar, m_ar))
    phb = np.array([[(i+1) for j in range(m_br)] for i in range(m_br)])
    phc = np.zeros((m_ar, m_br))

    start_time = time.time()

    for i0 in range(0, m_ar, bk_size):
        for j0 in range(0, m_br, bk_size):
            for k0 in range(0, m_ar, bk_size):
                phc[i0:i0+bk_size, j0:j0+bk_size] += np.dot(
                    pha[i0:i0+bk_size, k0:k0+bk_size],
                    phb[k0:k0+bk_size, j0:j0+bk_size]
                )

    end_time = time.time()

    elapsed_time = end_time - start_time
    print("Time: {:.3f} seconds".format(elapsed_time))

    # Display 10 elements of the result matrix to verify correctness
    """ print("Result matrix:")
    for i in range(1):
        print(phc[i, :min(10, m_br)]) """

def measure_time(func, *args):
    start_time = time.time()
    result = func(*args)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print("Time: {:.3f} seconds".format(elapsed_time))
    return result

def test_on_mult():
    print("Testing on_mult:\n")
    for i in range(600, 3001, 400):
        print(f"{i} * {i}")
        for _ in range(3):
            on_mult(i, i)
        print()
    print()

def test_on_mult_line():
    print("Testing on_mult_line:\n")
    for i in range(600, 3001, 400):
        print(f"{i} * {i}")
        for _ in range(3):
            on_mult_line(i, i)
        print()
    print()

def test_on_mult_block():
    print("Testing on_mult_block:")
    for i in range(4096, 10241, 2048):
        for j in range(128, 513, 128):
            print(f"{i} * {i}, block size: {j}")
            for _ in range(3):
                measure_time(on_mult_block_parallel, i, i, j)


def main():
    test_on_mult()
    test_on_mult_line()
    test_on_mult_block()

main()