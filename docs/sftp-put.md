```
USAGE:
    sftp-put <mediaflux-arguments> <sftp-arguments>

DESCRIPTION:
    Export Mediaflux assets to remote SFTP server using sftp.

MEDIAFLUX ARGUMENTS:
    --mf.host <host>                      Mediaflux server host.
    --mf.port <port>                      Mediaflux server port.
    --mf.transport <https|http|tcp/ip>    Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>      Mediaflux user credentials.
    --mf.token <token>                    Mediaflux secure identity token.
    --mf.async                            Executes the job in the background. The background service can be checked by executing service.background.describe service in Mediaflux Aterm.
    --mf.namespace <src-namespace>        Source namespace on Mediaflux.
    --mf.unarchive                        Unpack asset contents.

SFTP ARGUMENTS:
    --ssh.host <host>                     SFTP server host.
    --ssh.port <port>                     SFTP server port. Optional. Defaults to 22.
    --ssh.user <username>                 SFTP user name.
    --ssh.password <password>             SFTP user's password.
    --ssh.private-key <private-key>       SFTP user's private key.
    --ssh.passphrase <passphrase>         Passphrase for the SFTP user's private key.
    --ssh.directory <dst-directory>       Destination directory on remote SFTP server.

EXAMPLES:
    The command below exports assets from the specified Mediaflux asset namespace to remote sftp server:
        sftp-put --mf.host mediaflux.your-domain.org --mf.port 443 --mf.transport 443 --mf.auth mf_domain,mf_user,MF_PASSWD --mf.namespace /path/to/src-namespace --ssh.host ssh-server.your-domain.org --ssh.port 22 --ssh.user ssh_username --ssh.password SSH_PASSWD --ssh.directory path/to/dst-directory
```
