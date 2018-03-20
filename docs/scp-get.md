```
NAME
    scp-get

SYNOPSIS
    scp-get <mediaflux-arguments> <scp-arguments>

DESCRIPTION
    Import files from remote SSH server to Mediaflux using scp.

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

SCP ARGUMENTS:
    --ssh.host <host>                     SSH server host.
    --ssh.port <port>                     SSH server port. Optional. Defaults to 22.
    --ssh.user <username>                 SSH user name.
    --ssh.password <password>             SSH user's password.
    --ssh.private-key <private-key>       SSH user's private key.
    --ssh.passphrase <passphrase>         Passphrase for the SSH user's private key.
    --ssh.path <src-path>                 Source path on remote SSH server.
```
