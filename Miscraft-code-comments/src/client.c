#ifdef _WIN32
    #include <winsock2.h>
    #include <windows.h>
    #define close closesocket
    #define sleep Sleep
#else
    #include <netdb.h>
    #include <unistd.h>
#endif

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "client.h"
#include "tinycthread.h"

// "QUEUE_SIZE" is the number of chars in the send queue
#define QUEUE_SIZE 1048576
#define RECV_SIZE 4096

// Client state (not available to outside code)

static int client_enabled = 0;
static int running = 0;

// Socket descriptor
static int sd = 0;

static int bytes_sent = 0;
static int bytes_received = 0;
static char *queue = 0;
static int qsize = 0;
static thrd_t recv_thread;
static mtx_t mutex;

// Sets the client state to be enabled.
// Arguments: none
// Returns: none
void client_enable() {
    client_enabled = 1;
}

// Sets the client state to be disabled.
// Arguments: none
// Returns: none
void client_disable() {
    client_enabled = 0;
}

// Get whether the client is enabled or not.
// Arguments: none
// Returns:
// - boolean (non-zero) if the client is enabled
int get_client_enabled() {
    return client_enabled;
}

// Send all data socket descriptor.
// Not meant to usually be called directly, but meant to be called by client_send().
// Arguments:
// - sd: socket descriptor to send data through
// - data: string data to send
// - length: length of the string data
// Returns:
// - 0 upon completion/success
int client_sendall(int sd, char *data, int length) {
    if (!client_enabled) {
        return 0;
    }
    int count = 0;
    while (count < length) {
        int n = send(sd, data + count, length, 0);
        if (n == -1) {
            return -1;
        }
        count += n;
        length -= n;
        bytes_sent += n;
    }
    return 0;
}

// Client send a data string
// Arguments:
// - data
// Returns: none
void client_send(char *data) {
    if (!client_enabled) {
        return;
    }
    if (client_sendall(sd, data, strlen(data)) == -1) {
        perror("client_sendall");
        exit(1);
    }
}

// Client send version
// Arguments:
// - version
// Returns: none
void client_version(int version) {
    if (!client_enabled) {
        return;
    }
    char buffer[1024];
    snprintf(buffer, 1024, "V,%d\n", version);
    client_send(buffer);
}

// Client send authenticate login with identity
// Arguments:
// - username
// - identity_token
// Returns: none
void client_login(const char *username, const char *identity_token) {
    if (!client_enabled) {
        return;
    }
    char buffer[1024];
    snprintf(buffer, 1024, "A,%s,%s\n", username, identity_token);
    client_send(buffer);
}

// Client send player position
// Arguments:
// - x
// - y
// - z
// - rx
// - ry
// Returns: none
void client_position(float x, float y, float z, float rx, float ry) {
    if (!client_enabled) {
        return;
    }
    static float px, py, pz, prx, pry = 0;
    float distance =
        (px - x) * (px - x) +
        (py - y) * (py - y) +
        (pz - z) * (pz - z) +
        (prx - rx) * (prx - rx) +
        (pry - ry) * (pry - ry);
    if (distance < 0.0001) {
        return;
    }
    px = x; py = y; pz = z; prx = rx; pry = ry;
    char buffer[1024];
    snprintf(buffer, 1024, "P,%.2f,%.2f,%.2f,%.2f,%.2f\n", x, y, z, rx, ry);
    client_send(buffer);
}

// Client send request for chunk
// Arguments:
// - p
// - q
// - key
// Returns: none
void client_chunk(int p, int q, int key) {
    if (!client_enabled) {
        return;
    }
    char buffer[1024];
    snprintf(buffer, 1024, "C,%d,%d,%d\n", p, q, key);
    client_send(buffer);
}

// Client send block update
// Arguments:
// - x
// - y
// - z
// - w
// Returns: none
void client_block(int x, int y, int z, int w) {
    if (!client_enabled) {
        return;
    }
    char buffer[1024];
    snprintf(buffer, 1024, "B,%d,%d,%d,%d\n", x, y, z, w);
    client_send(buffer);
}

// Client send lighting update
// Arguments:
// - x
// - y
// - z
// - w
// Returns: none
void client_light(int x, int y, int z, int w) {
    if (!client_enabled) {
        return;
    }
    char buffer[1024];
    snprintf(buffer, 1024, "L,%d,%d,%d,%d\n", x, y, z, w);
    client_send(buffer);
}

// Client send sign creation
// Arguments:
// - x
// - y
// - z
// - face
// - text
// Returns: none
void client_sign(int x, int y, int z, int face, const char *text) {
    if (!client_enabled) {
        return;
    }
    char buffer[1024];
    snprintf(buffer, 1024, "S,%d,%d,%d,%d,%s\n", x, y, z, face, text);
    client_send(buffer);
}

// Client send chat message
// Arguments:
// - text
// Returns: none
void client_talk(const char *text) {
    if (!client_enabled) {
        return;
    }
    if (strlen(text) == 0) {
        return;
    }
    char buffer[1024];
    snprintf(buffer, 1024, "T,%s\n", text);
    client_send(buffer);
}

// Client receive data
// Arguments: none
// Returns:
// - ?
char *client_recv() {
    if (!client_enabled) {
        return 0;
    }
    char *result = 0;
    mtx_lock(&mutex);
    char *p = queue + qsize - 1;
    while (p >= queue && *p != '\n') {
        p--;
    }
    if (p >= queue) {
        int length = p - queue + 1;
        result = malloc(sizeof(char) * (length + 1));
        memcpy(result, queue, sizeof(char) * length);
        result[length] = '\0';
        int remaining = qsize - length;
        memmove(queue, p + 1, remaining);
        qsize -= length;
        bytes_received += length;
    }
    mtx_unlock(&mutex);
    return result;
}

// Receive worker
// Arguments:
// - arg
// Returns:
// - ?
int recv_worker(void *arg) {
    char *data = malloc(sizeof(char) * RECV_SIZE);
    while (1) {
        int length;
        if ((length = recv(sd, data, RECV_SIZE - 1, 0)) <= 0) {
            if (running) {
                perror("recv");
                exit(1);
            }
            else {
                break;
            }
        }
        data[length] = '\0';
        while (1) {
            int done = 0;
            mtx_lock(&mutex);
            if (qsize + length < QUEUE_SIZE) {
                memcpy(queue + qsize, data, sizeof(char) * (length + 1));
                qsize += length;
                done = 1;
            }
            mtx_unlock(&mutex);
            if (done) {
                break;
            }
            sleep(0);
        }
    }
    free(data);
    return 0;
}

// Client connect to server
// Note: this is where the socket descriptor "sd" is initialized.
// Arguments:
// - hostname
// - port
// Returns: none
void client_connect(char *hostname, int port) {
    if (!client_enabled) {
        return;
    }
    // Get host address
    struct hostent *host;
    struct sockaddr_in address;
    if ((host = gethostbyname(hostname)) == 0) {
        perror("gethostbyname");
        exit(1);
    }
    memset(&address, 0, sizeof(address));
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = ((struct in_addr *)(host->h_addr_list[0]))->s_addr;
    address.sin_port = htons(port);
    // Create socket
    if ((sd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("socket");
        exit(1);
    }
    // Connect
    if (connect(sd, (struct sockaddr *)&address, sizeof(address)) == -1) {
        perror("connect");
        exit(1);
    }
}

// Start the client.
// Arguments: none
// Returns: none
void client_start() {
    if (!client_enabled) {
        return;
    }
    running = 1;
    // Create the queue
    queue = (char *)calloc(QUEUE_SIZE, sizeof(char));
    qsize = 0;
    mtx_init(&mutex, mtx_plain);
    if (thrd_create(&recv_thread, recv_worker, NULL) != thrd_success) {
        perror("thrd_create");
        exit(1);
    }
}

// Stop the client.
// Arguments: none
// Returns: none
void client_stop() {
    if (!client_enabled) {
        return;
    }
    running = 0;
    close(sd);
    // if (thrd_join(recv_thread, NULL) != thrd_success) {
    //     perror("thrd_join");
    //     exit(1);
    // }
    // mtx_destroy(&mutex);
    qsize = 0;
    free(queue);
    // printf("Bytes Sent: %d, Bytes Received: %d\n",
    //     bytes_sent, bytes_received);
}

