```
NAME
    scp-put

SYNOPSIS
    scp-put <mediaflux-arguments> <scp-arguments>

DESCRIPTION
    Export Mediaflux assets to remote SSH server using scp.

MEDIAFLUX ARGUMENTS:
    --mf.host <host>                      Mediaflux server host.
    --mf.port <port>                      Mediaflux server port.
    --mf.transport <https|http|tcp/ip>    Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>      Mediaflux user credentials.
    --mf.token <token>                    Mediaflux secure identity token.
    --mf.async                            Executes the job in the background. The background service can be checked by executing service.background.describe service in Mediaflux Aterm.
    --mf.namespace <src-namespace>        Source namespace on Mediaflux.
    --mf.unarchive                        Unpack asset contents.

SCP ARGUMENTS:
    --ssh.host <host>                     SSH server host.
    --ssh.port <port>                     SSH server port. Optional. Defaults to 22.
    --ssh.user <username>                 SSH user name.
    --ssh.password <password>             SSH user's password.
    --ssh.private-key <private-key>       SSH user's private key.
    --ssh.passphrase <passphrase>         Passphrase for the SSH user's private key.
    --ssh.directory <dst-directory>       Destination directory on remote SSH server.
```
