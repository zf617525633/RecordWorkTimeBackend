#!/bin/bash
set -e

HOST="47.107.28.97"
USER="root"
PASS="Zf617525633!@#"

export SSHPASS="$PASS"
SSH_CMD="sshpass -e ssh -o StrictHostKeyChecking=no $USER@$HOST"
SCP_CMD="sshpass -e scp -o StrictHostKeyChecking=no"

echo "1. Cleaning up existing remote folders..."
$SSH_CMD "rm -rf /root/admin-dist /root/h5-dist"

echo "2. Uploading Admin Frontend..."
$SCP_CMD -r FissionSystemFrontendAdmin/dist $USER@$HOST:/root/admin-dist

echo "3. Uploading H5 Frontend..."
$SCP_CMD -r FissionSystemFrontendH5/dist $USER@$HOST:/root/h5-dist

echo "3. Uploading Backend Jar..."
$SCP_CMD FissionSystemBackend/target/backend-0.0.1-SNAPSHOT.jar $USER@$HOST:/root/

echo "4. Uploading SQL Dump (just in case)..."
$SCP_CMD fission_system_dump.sql $USER@$HOST:/root/

echo "5. Uploading Deploy Script..."
$SCP_CMD deploy.sh $USER@$HOST:/root/

echo "5.5 Uploading Secrets Configuration..."
$SCP_CMD application-secrets.yml $USER@$HOST:/root/

echo "6. Executing Deploy Script on Server..."
$SSH_CMD "chmod +x /root/deploy.sh && /root/deploy.sh"

echo "Deployment finished successfully!"
