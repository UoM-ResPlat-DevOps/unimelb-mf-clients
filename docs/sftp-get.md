```
USAGE:
    sftp-get <mediaflux-arguments> <sftp-arguments>

DESCRIPTION:
    Import files from remote SFTP server to Mediaflux using sftp.

MEDIAFLUX ARGUMENTS:
    --mf.host <host>                      Mediaflux server host.
    --mf.port <port>                      Mediaflux server port.
    --mf.transport <https|http|tcp/ip>    Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>      Mediaflux user credentials.
    --mf.token <token>                    Mediaflux secure identity token.
    --mf.async                            Executes the job in the background. The background service can be checked by executing service.background.describe service in Mediaflux Aterm.
    --mf.namespace <dst-namespace>        Destination namespace on Mediaflux.
    --mf.readonly                         Set the assets to be read-only.
    --mf.worm                             Set the assets to WORM state.
    --mf.worm.expiry <d-MMM-yyyy>         Set the assets WORM expiry date.

SFTP ARGUMENTS:
    --ssh.host <host>                     SFTP server host.
    --ssh.port <port>                     SFTP server port. Optional. Defaults to 22.
    --ssh.user <username>                 SFTP user name.
    --ssh.password <password>             SFTP user's password.
    --ssh.private-key <private-key>       SFTP user's private key.
    --ssh.passphrase <passphrase>         Passphrase for the SFTP user's private key.
    --ssh.path <src-path>                 Source path on remote SFTP server.

EXAMPLES:
    The command below imports files from sftp server into the specified Mediaflux asset namespace:
         sftp-get --mf.host mediaflux.your-domain.org --mf.port 443 --mf.transport 443 --mf.auth mf_domain,mf_user,MF_PASSWD --mf.namespace /path/to/dst-namespace --ssh.host ssh-server.your-domain.org --ssh.port 22 --ssh.user ssh_username --ssh.password SSH_PASSWD --ssh.path path/to/src-directory
```
